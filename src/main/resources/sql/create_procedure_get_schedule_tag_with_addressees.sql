CREATE OR REPLACE FUNCTION get_schedule_tag_with_addressees(p_schedule_tag_id bigint)
    RETURNS TABLE(
                    addressee_dtype character varying(31),
                    addressee_id bigint,
                    email character varying(255),
                    first_name character varying(255),
                    last_name character varying(255),

                 )
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
