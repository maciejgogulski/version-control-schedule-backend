DROP FUNCTION public.delete_block_parameter(p_modification_id bigint);

CREATE OR REPLACE FUNCTION public.delete_block_parameter(p_modification_id bigint) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE block_parameter
        SET deleted = false
        WHERE id = p_modification_id;
END;
$$;


ALTER FUNCTION public.delete_block_parameter(p_modification_id bigint) OWNER TO postgres;
