CREATE OR REPLACE FUNCTION assign_addressee_to_schedule_tag(p_addressee_id bigint, p_schedule_tag_id bigint)
    RETURNS VOID
AS
$$
BEGIN
    INSERT INTO schedule_tag_addressee
        (addressee_id, schedule_tag_id)
    VALUES (p_addressee_id, p_schedule_tag_id);
END;
$$
    LANGUAGE plpgsql;
