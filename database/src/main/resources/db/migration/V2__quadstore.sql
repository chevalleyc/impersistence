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
    id              UUID PRIMARY KEY,
    person_id       UUID,
    organisation_id UUID           NOT NULL,
    iri             TEXT,
    name            quadstore.coded_text NOT NULL,
    effective_date  TIMESTAMP,
    type            INTEGER,
    feeder_audit    JSONB,
    properties      JSONB,
    created_ts   TIMESTAMP NOT NULL DEFAULT current_timestamp
);

CREATE TABLE quadstore.node_history
(
    LIKE quadstore.node
);

CREATE TRIGGER versioning_trigger
    BEFORE INSERT OR UPDATE OR DELETE
    ON quadstore.node
    FOR EACH ROW
EXECUTE PROCEDURE ext.versioning('sys_period', 'ehr.status_history', true);

CREATE TABLE quadstore.quad
(
    id           UUID PRIMARY KEY,
    subject_id   UUID NOT NULL,
    predicate_id UUID NOT NULL,
    object_id    UUID NOT NULL,
    label_id     UUID,
    created_ts   TIMESTAMP NOT NULL DEFAULT current_timestamp
);


CREATE TABLE quadstore.quad_history
(
    LIKE quadstore.quad
);
CREATE TRIGGER versioning_trigger
    BEFORE INSERT OR UPDATE OR DELETE
    ON quadstore.quad
    FOR EACH ROW
EXECUTE PROCEDURE ext.versioning('sys_period', 'ehr.status_history', true);

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