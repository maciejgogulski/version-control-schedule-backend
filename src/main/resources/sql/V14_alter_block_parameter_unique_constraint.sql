ALTER TABLE block_parameter DROP CONSTRAINT unique_block_parameter;

CREATE UNIQUE INDEX unique_block_parameter
    ON block_parameter (schedule_block_id, parameter_dict_id)
    WHERE deleted = false;
