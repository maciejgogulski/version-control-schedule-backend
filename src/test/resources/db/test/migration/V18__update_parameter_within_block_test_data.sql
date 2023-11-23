-- Update parameter within schedule block

-- should CREATE_PARAMETER
-- case exists CREATE_PARAMETER_MODIFICATION
INSERT INTO schedule_tag
    (id, name)
VALUES
    (1, 'Test schedule');

INSERT INTO schedule_block
    (id, schedule_tag_id, name, start_date, end_date)
VALUES
    (1, 1, 'Test block', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
    (id, name)
VALUES
    (4, 'Room');

INSERT INTO staged_event
    (id, schedule_tag_id)
VALUES
    (1, 1);

INSERT INTO block_parameter
(id, parameter_dict_id, schedule_block_id, value)
VALUES
(1, 1, 1, '101');

INSERT INTO modification
    (id, staged_event_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (1, 1, 1, 'CREATE_PARAMETER', NULL, '101', CURRENT_TIMESTAMP)


