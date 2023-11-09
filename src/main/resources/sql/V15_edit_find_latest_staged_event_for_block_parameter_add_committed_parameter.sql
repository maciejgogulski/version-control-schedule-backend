CREATE OR REPLACE FUNCTION find_latest_staged_event_for_block_parameter(p_block_parameter_id bigint, p_committed boolean)
    RETURNS SETOF staged_event
AS
$$
BEGIN
    RETURN QUERY SELECT staged_event.id, staged_event.schedule_tag_id, committed, timestamp
                 FROM staged_event
                          LEFT JOIN schedule_tag on staged_event.schedule_tag_id = schedule_tag.id
                          LEFT JOIN schedule_block on schedule_tag.id = schedule_block.schedule_tag_id
                          LEFT JOIN block_parameter on schedule_block.id = block_parameter.schedule_block_id
                 WHERE block_parameter.id = p_block_parameter_id
                 AND staged_event.committed = p_committed
                 ORDER BY timestamp desc
                 LIMIT 1;
END;
$$
    LANGUAGE plpgsql;