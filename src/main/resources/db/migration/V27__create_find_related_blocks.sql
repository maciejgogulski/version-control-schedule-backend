CREATE OR REPLACE FUNCTION public.find_related_blocks(p_block_id bigint)
    RETURNS TABLE
            (
                id          bigint,
                schedule_id bigint,
                name        varchar(255),
                start_date  timestamp(6),
                end_date    timestamp(6)
            )
    LANGUAGE plpgsql
AS
$$
DECLARE
    v_schedule_id bigint;
    v_block_name  varchar(255);
BEGIN
    SELECT schedule.id
    INTO v_schedule_id
    FROM schedule
             INNER JOIN block ON block.schedule_id = schedule.id
    WHERE block.id = p_block_id
    LIMIT 1;

    SELECT block.name
    INTO v_block_name
    FROM block
    WHERE block.id = p_block_id
    LIMIT 1;

    RETURN QUERY
        SELECT block.id, block.schedule_id, block.name, block.start_date, block.end_date
        FROM block
        WHERE block.schedule_id = v_schedule_id
          AND block.name = v_block_name
          AND block.deleted = false
        ORDER BY block.start_date;
END;
$$;

