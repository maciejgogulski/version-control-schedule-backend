CREATE OR REPLACE FUNCTION get_modifications_for_staged_event(p_staged_event_id bigint)
    RETURNS TABLE
            (
                id                 bigint,
                staged_event_id    bigint,
                block_parameter_id bigint,
                block_name         varchar(255),
                parameter_name     varchar(100),
                type               varchar(10),
                old_value          varchar(1000),
                new_value          varchar(1000)
            )
AS
$$
BEGIN
    RETURN QUERY SELECT modification.id,
                        modification.staged_event_id,
                        modification.block_parameter_id,
                        schedule_block.name,
                        parameter_dict.name,
                        modification.type,
                        modification.old_value,
                        modification.new_value
                 FROM modification
                          INNER JOIN staged_event on modification.staged_event_id = staged_event.id
                          INNER JOIN block_parameter on modification.block_parameter_id = block_parameter.id
                          INNER JOIN schedule_block on block_parameter.schedule_block_id = schedule_block.id
                          INNER JOIN parameter_dict on block_parameter.parameter_dict_id = parameter_dict.id
                 WHERE modification.staged_event_id = p_staged_event_id;
END;
$$
    LANGUAGE plpgsql;
