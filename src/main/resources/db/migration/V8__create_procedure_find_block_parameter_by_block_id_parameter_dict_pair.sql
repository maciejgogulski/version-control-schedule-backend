CREATE OR REPLACE FUNCTION find_deleted_block_parameter_by_block_id_parameter_dict_pair(p_block_id bigint, p_parameter_dict_id bigint)
    RETURNS SETOF public.block_parameter
AS
$$
BEGIN
    RETURN QUERY SELECT id, parameter_dict_id, block_id, value, deleted
                 FROM block_parameter
                 WHERE block_parameter.block_id = p_block_id
                   AND block_parameter.parameter_dict_id = p_parameter_dict_id
                   AND block_parameter.deleted = true
                 LIMIT 1;
END;
$$
    LANGUAGE plpgsql;
