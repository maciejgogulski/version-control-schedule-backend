---------------------------------------------------------------------------
-- DELETE PARAMETER FROM BLOCK TEST DATA
---------------------------------------------------------------------------

--------------------------------------------------
-- 1. givenNoPreviousMod_whenDeleteParamFromBlock_thenThrowEntityNotFoundException
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 12');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (12, 'Test block 12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Employee');

INSERT INTO version
(schedule_id)
VALUES
    (12);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (15, 12, 'Jan Kowalski');

--------------------------------------------------
-- 2. givenPreviousModDeleteParam_whenDeleteParamFromBlock_thenThrowIllegalStateException
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 13');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (13, 'Test block 13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Series');

INSERT INTO version
(schedule_id)
VALUES
    (13);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (16, 13, 'Lenovo Thinkpad');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
(16, 13, 'DELETE_PARAMETER', 'Lenovo Thinkpad', NULL, CURRENT_TIMESTAMP);

--------------------------------------------------
-- 3. givenPreviousModUpdateParamInPreviousVersion_whenDeleteParamFromBlock_thenCreateModDeleteParam
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 14');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (14, 'Test block 14', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Direction');

INSERT INTO version
(schedule_id)
VALUES
    (14);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (17, 14, 'North');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (17, 14, 'UPDATE_PARAMETER', 'West', 'North', CURRENT_TIMESTAMP);

UPDATE version
SET committed = true
WHERE version.id = 17;

INSERT INTO version
(schedule_id)
VALUES
    (14);

--------------------------------------------------
-- 4. givenPreviousModeCreateParamInCurrentVersion_whenDeleteParamFromBlock_thenDeleteMod
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 15');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (15, 'Test block 15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Height');

INSERT INTO version
(schedule_id)
VALUES
    (15);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (18, 15, 'Height');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (19, 15, 'CREATE_PARAMETER', NULL, '125', CURRENT_TIMESTAMP);

--------------------------------------------------
-- 5. givenPreviousModeUpdateParamInCurrentVersion_whenDeleteParamFromBlock_thenChangeModTypeToDeleteParam
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 16');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (16, 'Test block 16', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Temperature');

INSERT INTO version
(schedule_id)
VALUES
    (16);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (19, 16, 'Height');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (20, 16, 'UPDATE_PARAMETER', '60 F', '-1 C', CURRENT_TIMESTAMP);

