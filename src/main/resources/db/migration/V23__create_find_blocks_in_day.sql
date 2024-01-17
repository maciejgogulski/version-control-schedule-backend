CREATE OR REPLACE FUNCTION public.find_blocks_in_day(p_schedule_id bigint, p_start_of_day timestamp,
                                                     p_end_of_day timestamp)
    RETURNS TABLE
            (
                id          bigint,
                schedule_id bigint,
                name varchar(255),
                start_date  timestamp(6),
                end_date    timestamp(6)
            )
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY
        SELECT block.id, block.schedule_id, block.name, block.start_date, block.end_date
        FROM block
        WHERE block.schedule_id = p_schedule_id
          AND block.start_date BETWEEN p_start_of_day AND p_end_of_day
          AND block.deleted = false
        ORDER BY block.start_date;
END;
$$;

