CREATE OR REPLACE FUNCTION public.delete_block(p_block_id bigint) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE block
        SET deleted = true
        WHERE id = p_block_id;
END;
$$;

ALTER FUNCTION public.delete_block(p_block_id bigint) OWNER TO postgres;
