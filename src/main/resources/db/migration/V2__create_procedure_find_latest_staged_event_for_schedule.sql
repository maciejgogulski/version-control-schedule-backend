CREATE FUNCTION public.find_latest_staged_event_for_schedule(p_schedule_tag_id bigint) RETURNS SETOF public.staged_event
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT staged_event.id, staged_event.schedule_tag_id, committed, timestamp
                 FROM staged_event
                          LEFT JOIN schedule_tag on staged_event.schedule_tag_id = schedule_tag.id
                 WHERE schedule_tag.id = p_schedule_tag_id
                 ORDER BY timestamp desc
                 LIMIT 1;
END;
$$;


ALTER FUNCTION public.find_latest_staged_event_for_schedule(p_block_parameter_id bigint) OWNER TO postgres;
