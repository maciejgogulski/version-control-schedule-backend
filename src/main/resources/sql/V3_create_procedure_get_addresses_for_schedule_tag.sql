CREATE OR REPLACE FUNCTION get_addressees_for_schedule_tag(p_schedule_tag_id bigint)
    RETURNS SETOF addressee
AS
$$
BEGIN
    RETURN QUERY SELECT addressee.dtype, addressee.id, email, first_name, last_name, password, user_name
                 FROM addressee
                          INNER JOIN schedule_tag_addressee sta on addressee.id = sta.addressee_id
                 WHERE sta.schedule_tag_id = p_schedule_tag_id;
END;

$$
    LANGUAGE plpgsql;
