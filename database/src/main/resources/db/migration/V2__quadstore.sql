-- load the temporal_tables PLPG/SQL functions to emulate the coded extension
-- original source: https://github.com/nearform/temporal_tables/blob/master/versioning_function.sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA ext;

CREATE OR REPLACE FUNCTION ext.versioning()
    RETURNS TRIGGER AS $$
DECLARE
    sys_period text;
    history_table text;
    manipulate jsonb;
    ignore_unchanged_values bool;
    commonColumns text[];
    time_stamp_to_use timestamptz := current_timestamp;
    range_lower timestamptz;
    transaction_info txid_snapshot;
    existing_range tstzrange;
    holder record;
    holder2 record;
    pg_version integer;
BEGIN
    -- version 0.4.0

    IF TG_WHEN != 'BEFORE' OR TG_LEVEL != 'ROW' THEN
        RAISE TRIGGER_PROTOCOL_VIOLATED USING
            MESSAGE = 'function "versioning" must be fired BEFORE ROW';
    END IF;

    IF TG_OP != 'INSERT' AND TG_OP != 'UPDATE' AND TG_OP != 'DELETE' THEN
        RAISE TRIGGER_PROTOCOL_VIOLATED USING
            MESSAGE = 'function "versioning" must be fired for INSERT or UPDATE or DELETE';
    END IF;

    IF TG_NARGS not in (3,4) THEN
        RAISE INVALID_PARAMETER_VALUE USING
            MESSAGE = 'wrong number of parameters for function "versioning"',
            HINT = 'expected 3 or 4 parameters but got ' || TG_NARGS;
    END IF;

    sys_period := TG_ARGV[0];
    history_table := TG_ARGV[1];
    ignore_unchanged_values := TG_ARGV[3];

    IF ignore_unchanged_values AND TG_OP = 'UPDATE' AND NEW IS NOT DISTINCT FROM OLD THEN
        RETURN OLD;
    END IF;

    -- check if sys_period exists on original table
    SELECT atttypid, attndims INTO holder FROM pg_attribute WHERE attrelid = TG_RELID AND attname = sys_period AND NOT attisdropped;
    IF NOT FOUND THEN
        RAISE 'column "%" of relation "%" does not exist', sys_period, TG_TABLE_NAME USING
            ERRCODE = 'undefined_column';
    END IF;
    IF holder.atttypid != to_regtype('tstzrange') THEN
        IF holder.attndims > 0 THEN
            RAISE 'system period column "%" of relation "%" is not a range but an array', sys_period, TG_TABLE_NAME USING
                ERRCODE = 'datatype_mismatch';
        END IF;

        SELECT rngsubtype INTO holder2 FROM pg_range WHERE rngtypid = holder.atttypid;
        IF FOUND THEN
            RAISE 'system period column "%" of relation "%" is not a range of timestamp with timezone but of type %', sys_period, TG_TABLE_NAME, format_type(holder2.rngsubtype, null) USING
                ERRCODE = 'datatype_mismatch';
        END IF;

        RAISE 'system period column "%" of relation "%" is not a range but type %', sys_period, TG_TABLE_NAME, format_type(holder.atttypid, null) USING
            ERRCODE = 'datatype_mismatch';
    END IF;

    IF TG_OP = 'UPDATE' OR TG_OP = 'DELETE' THEN
        -- Ignore rows already modified in this transaction
        transaction_info := txid_current_snapshot();
        IF OLD.xmin::text >= (txid_snapshot_xmin(transaction_info) % (2^32)::bigint)::text
            AND OLD.xmin::text <= (txid_snapshot_xmax(transaction_info) % (2^32)::bigint)::text THEN
            IF TG_OP = 'DELETE' THEN
                RETURN OLD;
            END IF;

            RETURN NEW;
        END IF;

        SELECT current_setting('server_version_num')::integer
        INTO pg_version;

        -- to support postgres < 9.6
        IF pg_version < 90600 THEN
            -- check if history table exits
            IF to_regclass(history_table::cstring) IS NULL THEN
                RAISE 'relation "%" does not exist', history_table;
            END IF;
        ELSE
            IF to_regclass(history_table) IS NULL THEN
                RAISE 'relation "%" does not exist', history_table;
            END IF;
        END IF;

        -- check if history table has sys_period
        IF NOT EXISTS(SELECT * FROM pg_attribute WHERE attrelid = history_table::regclass AND attname = sys_period AND NOT attisdropped) THEN
            RAISE 'history relation "%" does not contain system period column "%"', history_table, sys_period USING
                HINT = 'history relation must contain system period column with the same name and data type as the versioned one';
        END IF;

        EXECUTE format('SELECT $1.%I', sys_period) USING OLD INTO existing_range;

        IF existing_range IS NULL THEN
            RAISE 'system period column "%" of relation "%" must not be null', sys_period, TG_TABLE_NAME USING
                ERRCODE = 'null_value_not_allowed';
        END IF;

        IF isempty(existing_range) OR NOT upper_inf(existing_range) THEN
            RAISE 'system period column "%" of relation "%" contains invalid value', sys_period, TG_TABLE_NAME USING
                ERRCODE = 'data_exception',
                DETAIL = 'valid ranges must be non-empty and unbounded on the high side';
        END IF;

        IF TG_ARGV[2] = 'true' THEN
            -- mitigate update conflicts
            range_lower := lower(existing_range);
            IF range_lower >= time_stamp_to_use THEN
                time_stamp_to_use := range_lower + interval '1 microseconds';
            END IF;
        END IF;

        WITH history AS
                 (SELECT attname, atttypid
                  FROM   pg_attribute
                  WHERE  attrelid = history_table::regclass
                    AND    attnum > 0
                    AND    NOT attisdropped),
             main AS
                 (SELECT attname, atttypid
                  FROM   pg_attribute
                  WHERE  attrelid = TG_RELID
                    AND    attnum > 0
                    AND    NOT attisdropped)
        SELECT
            history.attname AS history_name,
            main.attname AS main_name,
            history.atttypid AS history_type,
            main.atttypid AS main_type
        INTO holder
        FROM history
                 INNER JOIN main
                            ON history.attname = main.attname
        WHERE
                history.atttypid != main.atttypid;

        IF FOUND THEN
            RAISE 'column "%" of relation "%" is of type % but column "%" of history relation "%" is of type %',
                holder.main_name, TG_TABLE_NAME, format_type(holder.main_type, null), holder.history_name, history_table, format_type(holder.history_type, null)
                USING ERRCODE = 'datatype_mismatch';
        END IF;

        WITH history AS
                 (SELECT attname
                  FROM   pg_attribute
                  WHERE  attrelid = history_table::regclass
                    AND    attnum > 0
                    AND    NOT attisdropped),
             main AS
                 (SELECT attname
                  FROM   pg_attribute
                  WHERE  attrelid = TG_RELID
                    AND    attnum > 0
                    AND    NOT attisdropped)
        SELECT array_agg(quote_ident(history.attname)) INTO commonColumns
        FROM history
                 INNER JOIN main
                            ON history.attname = main.attname
                                AND history.attname != sys_period;

        EXECUTE ('INSERT INTO ' ||
                 history_table ||
                 '(' ||
                 array_to_string(commonColumns , ',') ||
                 ',' ||
                 quote_ident(sys_period) ||
                 ') VALUES ($1.' ||
                 array_to_string(commonColumns, ',$1.') ||
                 ',tstzrange($2, $3, ''[)''))')
            USING OLD, range_lower, time_stamp_to_use;
    END IF;

    IF TG_OP = 'UPDATE' OR TG_OP = 'INSERT' THEN
        manipulate := jsonb_set('{}'::jsonb, ('{' || sys_period || '}')::text[], to_jsonb(tstzrange(time_stamp_to_use, null, '[)')));

        RETURN jsonb_populate_record(NEW, manipulate);
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- UDT for CODE_PHRASE
create type quadstore.code_phrase as
(
    terminology_id_value text,
    code_string          text
);

-- UDT for DV_CODED_TEXT
create type quadstore.coded_text as
(
    value         text,
    defining_code quadstore.code_phrase,
    formatting    text,
    language      quadstore.code_phrase,
    encoding      quadstore.code_phrase,
    term_mapping  TEXT[] -- array : match, purpose: value, terminology, code, target: terminology, code, delimited by '|'
);

CREATE TABLE quadstore.node
(
    id              UUID PRIMARY KEY DEFAULT ext.uuid_generate_v4(),
    person_id       UUID,
    organisation_id UUID,
    iri             TEXT,
    name            quadstore.coded_text NOT NULL,
    effective_date  TIMESTAMP,
    type            INTEGER,
    feeder_audit    JSONB,
    properties      JSONB,
    sys_transaction   TIMESTAMP NOT NULL DEFAULT current_timestamp,
    sys_period tstzrange NOT NULL -- temporal table
);

CREATE TABLE quadstore.node_history
(
    LIKE quadstore.node
);

CREATE TRIGGER versioning_trigger
    BEFORE INSERT OR UPDATE OR DELETE
    ON quadstore.node
    FOR EACH ROW
EXECUTE PROCEDURE ext.versioning('sys_period', 'quadstore.node_history', true);

CREATE TABLE quadstore.quad
(
    id           UUID PRIMARY KEY DEFAULT ext.uuid_generate_v4(),
    subject_id   UUID NOT NULL,
    predicate_id UUID NOT NULL,
    object_id    UUID NOT NULL,
    label_id     UUID,
    sys_transaction   TIMESTAMP NOT NULL DEFAULT current_timestamp,
    sys_period tstzrange NOT NULL -- temporal table
);


CREATE TABLE quadstore.quad_history
(
    LIKE quadstore.quad
);
CREATE TRIGGER versioning_trigger
    BEFORE INSERT OR UPDATE OR DELETE
    ON quadstore.quad
    FOR EACH ROW
EXECUTE PROCEDURE ext.versioning('sys_period', 'quadstore.quad_history', true);

CREATE TABLE quadstore.graph_label
(
    id          UUID PRIMARY KEY,
    name        TEXT NOT NULL,
    created_ts  TIMESTAMP NOT NULL DEFAULT current_timestamp
);

CREATE UNIQUE INDEX quad_unique ON quadstore.quad (subject_id, predicate_id, object_id) WHERE label_id IS NULL;
CREATE UNIQUE INDEX labeled_quad_unique ON quadstore.quad (subject_id, predicate_id, object_id, label_id) WHERE quad.label_id IS NOT NULL;

-- FKs
ALTER TABLE quadstore.quad
    ADD CONSTRAINT subject_id_fk FOREIGN KEY (subject_id) REFERENCES quadstore.node (id);
ALTER TABLE quadstore.quad
    ADD CONSTRAINT predicate_id_fk FOREIGN KEY (predicate_id) REFERENCES quadstore.node (id);
ALTER TABLE quadstore.quad
    ADD CONSTRAINT object_id_fk FOREIGN KEY (object_id) REFERENCES quadstore.node (id);
ALTER TABLE quadstore.quad
    ADD CONSTRAINT label_id_fk FOREIGN KEY (label_id) REFERENCES quadstore.node (id);
ALTER TABLE quadstore.quad
    ADD CONSTRAINT label_graph_id_fk FOREIGN KEY (label_id) REFERENCES quadstore.graph_label (id);

-- referential integrity for non null person_id and organisation_id
ALTER TABLE quadstore.node
    ADD CONSTRAINT non_null_person_fk FOREIGN KEY (person_id) REFERENCES quadstore.node (id);

ALTER TABLE quadstore.node
    ADD CONSTRAINT non_null_organisation_fk FOREIGN KEY (organisation_id) REFERENCES quadstore.node (id);

-- TODO: add check constraints as required (for person_id and organisation_id)