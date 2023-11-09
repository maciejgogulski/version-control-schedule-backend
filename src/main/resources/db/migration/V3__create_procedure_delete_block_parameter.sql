CREATE FUNCTION public.delete_block_parameter(p_modification_id bigint) RETURNS SETOF public.staged_event
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY UPDATE block_parameter
        SET deleted = false
        WHERE id = p_modification_id;
END;
$$;


ALTER FUNCTION public.delete_block_parameter(p_modification_id bigint) OWNER TO postgres;
