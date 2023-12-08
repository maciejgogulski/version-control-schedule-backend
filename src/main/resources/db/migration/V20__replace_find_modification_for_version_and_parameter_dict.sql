DROP FUNCTION public.find_modification_for_version_and_parameter_dict(p_version_id bigint, p_block_id bigint, p_parameter_dict_id bigint);

CREATE OR REPLACE FUNCTION public.find_modification_for_parameter_dict_with_latest_version(p_block_id bigint, p_parameter_dict_id bigint)
    RETURNS TABLE
            (
                modification_id                 bigint,
                modification_block_parameter_id bigint,
                modification_type               varchar(30),
                modification_old_value          varchar(1000),
                modification_new_value          varchar(1000),
                modification_timestamp          timestamp,
                version_id                      bigint,
                version_schedule_id             bigint,
                version_committed               boolean,
                version_timestamp               timestamp
            )
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY SELECT modification.id,
                        modification.block_parameter_id,
                        modification.type,
                        modification.old_value,
                        modification.new_value,
                        modification.timestamp,
                        version.id,
                        version.schedule_id,
                        version.committed,
                        version.timestamp
                 FROM modification
                          LEFT JOIN block_parameter on modification.block_parameter_id = block_parameter.id
                          LEFT JOIN version on modification.version_id = version.id
                 WHERE parameter_dict_id = p_parameter_dict_id
                   AND block_parameter.block_id = p_block_id
                   AND version.committed = true
                 ORDER BY version.timestamp DESC
                 LIMIT 1;
END;
$$;
