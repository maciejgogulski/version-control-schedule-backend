CREATE TABLE schedule_tag_addressee
(
    id              BIGSERIAL PRIMARY KEY,
    schedule_tag_id bigint NOT NULL,
    addressee_id    bigint NOT NULL,
    CONSTRAINT fk_schedule_tag
        FOREIGN KEY (schedule_tag_id)
            REFERENCES schedule_tag (id),
    CONSTRAINT fk_addressee_id
        FOREIGN KEY (addressee_id)
            REFERENCES addressee (id),
    CONSTRAINT unique_schedule_tag_addressee
        UNIQUE (schedule_tag_id, addressee_id)
);