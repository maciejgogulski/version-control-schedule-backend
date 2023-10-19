CREATE TABLE block_parameter
(
    id                BIGSERIAL PRIMARY KEY,
    parameter_dict_id bigint        NOT NULL,
    schedule_block_id bigint        NOT NULL,
    value             varchar(1000) NOT NULL,
    CONSTRAINT fk_schedule_block
        FOREIGN KEY (schedule_block_id)
            REFERENCES schedule_block (id),
    CONSTRAINT fk_parameter_dict_id
        FOREIGN KEY (parameter_dict_id)
            REFERENCES parameter_dict (id),
    CONSTRAINT unique_block_parameter
        UNIQUE (schedule_block_id, parameter_dict_id)
);