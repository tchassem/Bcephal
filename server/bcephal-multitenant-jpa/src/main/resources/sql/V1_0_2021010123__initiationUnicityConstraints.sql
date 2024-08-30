--ALTER TABLE BCP_MODEL ADD CONSTRAINT unique_model_name UNIQUE(name);
--ALTER TABLE BCP_ENTITY ADD CONSTRAINT unique_entity_name UNIQUE(name);
--ALTER TABLE BCP_ATTRIBUTE ADD CONSTRAINT unique_attribute_name UNIQUE(name);
--ALTER TABLE BCP_MEASURE ADD CONSTRAINT unique_measure_name UNIQUE(name);
--ALTER TABLE BCP_PERIOD_NAME ADD CONSTRAINT unique_period_name UNIQUE(name);

CREATE OR REPLACE FUNCTION add_constraint(t_name text, c_name text, constraint_sql text)
RETURNS void
AS $$
BEGIN
    IF NOT EXISTS(
            SELECT FROM pg_constraint 
                   WHERE conrelid = t_name::regclass AND conname = c_name
    ) THEN

        EXECUTE 'ALTER TABLE ' || t_name || ' ADD CONSTRAINT ' || c_name || ' ' || constraint_sql;

    END IF;
END;
$$
LANGUAGE plpgsql;

SELECT add_constraint('BCP_MODEL', 'unique_model_name', 'UNIQUE(name);');
SELECT add_constraint('BCP_ENTITY', 'unique_entity_name', 'UNIQUE(name);');
SELECT add_constraint('BCP_ATTRIBUTE', 'unique_attribute_name', 'UNIQUE(name);');
SELECT add_constraint('BCP_MEASURE', 'unique_measure_name', 'UNIQUE(name);');