CREATE OR REPLACE FUNCTION get_parameters_for_schedule_block(p_schedule_block_id bigint)
    RETURNS TABLE
            (
                id                bigint,
                schedule_block_id bigint,
                parameter_name    varchar(100),
                value             varchar(1000)
            )
AS
$$
BEGIN
    RETURN QUERY SELECT block_parameter.id, schedule_block.id, parameter_dict.name, block_parameter.value
                 FROM block_parameter
                          INNER JOIN schedule_block on block_parameter.schedule_block_id = schedule_block.id
                          INNER JOIN parameter_dict on block_parameter.parameter_dict_id = parameter_dict.id
                 WHERE schedule_block.id = p_schedule_block_id
                   AND block_parameter.deleted = false;
END;
$$
    LANGUAGE plpgsql;
