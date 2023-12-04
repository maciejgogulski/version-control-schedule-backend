CREATE OR REPLACE FUNCTION public.get_addressees_for_schedule(p_schedule_id bigint) RETURNS SETOF public.addressee
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY SELECT addressee.id, email, first_name, last_name, password, user_name, addressee.deleted
                 FROM addressee
                          INNER JOIN schedule_addressee sta on addressee.id = sta.addressee_id
                 WHERE sta.schedule_id = p_schedule_id;
END;
$$;
