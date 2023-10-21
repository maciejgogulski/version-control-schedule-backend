CREATE TABLE modification
(
    id                 BIGSERIAL PRIMARY KEY,
    staged_event_id    bigint        NOT NULL,
    block_parameter_id bigint        NOT NULL,
    type               varchar(10)   NOT NULL,
    old_value          varchar(1000),
    new_value          varchar(1000) NOT NULL,
    timestamp          timestamp     NOT NULL,
    CONSTRAINT fk_staged_event
        FOREIGN KEY (staged_event_id)
            REFERENCES staged_event (id),
    CONSTRAINT fk_block_parameter_id
        FOREIGN KEY (block_parameter_id)
            REFERENCES block_parameter (id),
    CONSTRAINT unique_modification
        UNIQUE (staged_event_id, block_parameter_id)
);