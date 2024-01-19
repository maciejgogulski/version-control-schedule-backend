CREATE OR REPLACE FUNCTION public.find_block_by_id(p_block_id bigint)
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
BEGIN
    RETURN QUERY
    SELECT block.id, block.schedule_id, block.name, block.start_date, block.end_date
        FROM block
    WHERE block.id = p_block_id;
END;
$$;

ALTER FUNCTION public.find_block_by_id(p_block_id bigint) OWNER TO postgres;
