CREATE OR REPLACE FUNCTION public.commit_staged_event(p_staged_event_id bigint) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE staged_event
        SET committed = true
        WHERE id = p_staged_event_id;
END;
$$;


ALTER FUNCTION public.commit_staged_event(p_staged_event_id bigint) OWNER TO postgres;
