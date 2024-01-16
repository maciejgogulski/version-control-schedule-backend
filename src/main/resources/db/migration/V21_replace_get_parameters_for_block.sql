CREATE OR REPLACE FUNCTION public.get_parameters_for_block(p_block_id bigint)
    RETURNS TABLE (id bigint, block_id bigint, parameter_name character varying, value character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT block_parameter.id, block.id, parameter_dict.name, block_parameter.value
FROM block_parameter
         INNER JOIN block ON block_parameter.block_id = block.id
         INNER JOIN parameter_dict ON block_parameter.parameter_dict_id = parameter_dict.id
WHERE block.id = p_block_id
  AND block_parameter.deleted = false
ORDER BY CASE
             WHEN parameter_dict.name = 'Name' THEN 1
             WHEN parameter_dict.name = 'Start date' THEN 2
             WHEN parameter_dict.name = 'End date' THEN 3
             ELSE 4
             END;
END;
$$;

