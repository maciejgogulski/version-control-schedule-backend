DROP FUNCTION get_modifications_for_version(p_version_id bigint);
CREATE OR REPLACE FUNCTION get_modifications_for_version(p_version_id bigint)
    RETURNS TABLE
            (
                id                 bigint,
                version_id    bigint,
                block_parameter_id bigint,
                block_name         character varying,
                parameter_name     character varying,
                type               character varying,
                old_value          character varying,
                new_value          character varying,
                "timestamp"        timestamp
            )
    language plpgsql
as
$$
BEGIN
    RETURN QUERY SELECT modification.id,
                        modification.version_id,
                        modification.block_parameter_id,
                        block.name,
                        parameter_dict.name,
                        modification.type,
                        modification.old_value,
                        modification.new_value,
                        modification."timestamp"
                 FROM modification
                          INNER JOIN version on modification.version_id = version.id
                          INNER JOIN block_parameter on modification.block_parameter_id = block_parameter.id
                          INNER JOIN block on block_parameter.block_id = block.id
                          INNER JOIN parameter_dict on block_parameter.parameter_dict_id = parameter_dict.id
                 WHERE modification.version_id = p_version_id
                 ORDER BY modification."timestamp" DESC;
END;
$$;
