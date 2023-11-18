DROP FUNCTION get_modifications_for_staged_event(p_staged_event_id bigint);
CREATE OR REPLACE FUNCTION get_modifications_for_staged_event(p_staged_event_id bigint)
    RETURNS TABLE
            (
                id                 bigint,
                staged_event_id    bigint,
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
                        modification.staged_event_id,
                        modification.block_parameter_id,
                        schedule_block.name,
                        parameter_dict.name,
                        modification.type,
                        modification.old_value,
                        modification.new_value,
                        modification."timestamp"
                 FROM modification
                          INNER JOIN staged_event on modification.staged_event_id = staged_event.id
                          INNER JOIN block_parameter on modification.block_parameter_id = block_parameter.id
                          INNER JOIN schedule_block on block_parameter.schedule_block_id = schedule_block.id
                          INNER JOIN parameter_dict on block_parameter.parameter_dict_id = parameter_dict.id
                 WHERE modification.staged_event_id = p_staged_event_id
                 ORDER BY modification."timestamp" DESC;
END;
$$;