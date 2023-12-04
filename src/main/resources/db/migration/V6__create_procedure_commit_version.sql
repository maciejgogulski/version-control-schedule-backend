CREATE OR REPLACE FUNCTION public.commit_version(p_version_id bigint) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE version
        SET committed = true
        WHERE id = p_version_id;
END;
$$;


ALTER FUNCTION public.commit_version(p_version_id bigint) OWNER TO postgres;
