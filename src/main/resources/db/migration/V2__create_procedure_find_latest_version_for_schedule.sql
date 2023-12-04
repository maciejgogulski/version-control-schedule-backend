CREATE FUNCTION public.find_latest_version_for_schedule(p_schedule_id bigint) RETURNS SETOF public.version
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT version.id, version.schedule_id, committed, timestamp
                 FROM version
                          LEFT JOIN schedule on version.schedule_id = schedule.id
                 WHERE schedule.id = p_schedule_id
                 ORDER BY timestamp desc
                 LIMIT 1;
END;
$$;


ALTER FUNCTION public.find_latest_version_for_schedule(p_block_parameter_id bigint) OWNER TO postgres;
