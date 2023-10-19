CREATE TABLE staged_event
(
    id              BIGSERIAL PRIMARY KEY,
    schedule_tag_id bigint NOT NULL,
    committed       bool NOT NULL DEFAULT false,
    timestamp       timestamp NOT NULL DEFAULT current_timestamp,
    CONSTRAINT fk_schedule_tag
        FOREIGN KEY (schedule_tag_id)
            REFERENCES schedule_tag (id)
);