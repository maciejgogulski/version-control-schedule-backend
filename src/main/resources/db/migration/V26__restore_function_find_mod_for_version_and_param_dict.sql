CREATE OR REPLACE FUNCTION public.find_modification_for_version_and_parameter_dict(p_version_id bigint, p_block_id bigint, p_parameter_dict_id bigint) RETURNS SETOF public.modification
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT modification.id, version_id, block_parameter_id, type, old_value, new_value, timestamp
                 FROM modification
                          LEFT JOIN block_parameter on modification.block_parameter_id = block_parameter.id
                 WHERE version_id = p_version_id
                   AND parameter_dict_id = p_parameter_dict_id
                   AND block_parameter.block_id = p_block_id
                 LIMIT 1;
END;
$$;
