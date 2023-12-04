-- Update parameter within schedule block

-- should CREATE_PARAMETER
INSERT INTO schedule
    (name)
VALUES
    ('Test schedule');

INSERT INTO block
    (schedule_id, name, start_date, end_date)
VALUES
    (1, 'Test block', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
    (name)
VALUES
    ('Room');

INSERT INTO version
    (schedule_id)
VALUES
    (1);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
(4, 1, '101');

INSERT INTO modification
    (version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (1, 1, 'CREATE_PARAMETER', NULL, '101', CURRENT_TIMESTAMP);


-----------------------------------------------------------------------------------------------
-- should UPDATE_PARAMETER
-----------------------------------------------------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 2');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (2, 'Test block 2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


INSERT INTO version
(schedule_id, committed)
VALUES
    (2, true);

INSERT INTO parameter_dict
(name)
VALUES
    ('Mode');

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (5, 2, 'Remote');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (2, 2, 'CREATE_PARAMETER', NULL, 'Remote', CURRENT_TIMESTAMP);

INSERT INTO version
(schedule_id)
VALUES
    (2);

-----------------------------------------------------------------------------------------------
-- should delete modification
-----------------------------------------------------------------------------------------------
INSERT INTO parameter_dict
(name)
VALUES
    ('Teacher');

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (6, 2, 'Mark Robertson');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (3, 3, 'UPDATE_PARAMETER', 'Robert Markson', 'Mark Robertson', CURRENT_TIMESTAMP);

-----------------------------------------------------------------------------------------------
-- should not create modification
-----------------------------------------------------------------------------------------------
INSERT INTO parameter_dict
(name)
VALUES
    ('Purpose');

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (7, 2, 'Learn students XYZ');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (2, 4, 'CREATE_PARAMETER', NULL, 'Learn students XYZ', CURRENT_TIMESTAMP);



