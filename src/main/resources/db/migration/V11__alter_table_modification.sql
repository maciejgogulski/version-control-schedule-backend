ALTER TABLE message
DROP COLUMN accepted_by_addressee,
DROP CONSTRAINT fkn49j1k5pgk4e0h71etqxc0r4g,
DROP COLUMN event_id,
ADD COLUMN staged_event_id bigint,
ADD CONSTRAINT fk_message_staged_event
    FOREIGN KEY (staged_event_id) REFERENCES staged_event (id);
