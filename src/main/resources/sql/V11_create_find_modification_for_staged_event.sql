CREATE OR REPLACE FUNCTION find_modification_for_staged_event_and_parameter_dict(p_staged_event_id bigint, p_schedule_block_id bigint, p_parameter_dict_id bigint)
    RETURNS SETOF modification
AS
$$
BEGIN
    RETURN QUERY SELECT modification.id, staged_event_id, block_parameter_id, type, old_value, new_value, timestamp
                 FROM modification
                    LEFT JOIN block_parameter on modification.block_parameter_id = block_parameter.id
                 WHERE staged_event_id = p_staged_event_id
                 AND parameter_dict_id = p_parameter_dict_id
                 AND block_parameter.schedule_block_id = p_schedule_block_id
                 AND block_parameter.deleted = false
                 LIMIT 1;
END;
$$
    LANGUAGE plpgsql;
