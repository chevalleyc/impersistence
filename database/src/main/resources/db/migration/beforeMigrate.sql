-- create roles (you might see an error here, these can be ignored)
DO $$
    BEGIN
        CREATE ROLE quadsmgr WITH LOGIN PASSWORD 'quadsmgr';
    EXCEPTION WHEN duplicate_object THEN RAISE NOTICE '%, moving to next statement', SQLERRM USING ERRCODE = SQLSTATE;
    END
$$;

-- install the extensions
CREATE SCHEMA IF NOT EXISTS quadstore AUTHORIZATION quadsmgr;
CREATE SCHEMA IF NOT EXISTS ext AUTHORIZATION quadsmgr;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA ext;
CREATE EXTENSION IF NOT EXISTS "ltree" SCHEMA ext;

-- setup the search_patch so the extensions can be found
SET search_path TO "$user",public,ext;
-- ensure INTERVAL is ISO8601 encoded
SET intervalstyle = 'iso_8601';

GRANT ALL ON ALL FUNCTIONS IN SCHEMA ext TO quadsmgr;