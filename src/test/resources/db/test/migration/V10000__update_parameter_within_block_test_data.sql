---------------------------------------------------------------------------
-- ASSIGN PARAMETER TO SCHEDULE TEST DATA
---------------------------------------------------------------------------

--------------------------------------------------
-- 1. givenNewBlockParameter_whenAssignParamToSchedule_thenCreateModCreateParam
--------------------------------------------------
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

--------------------------------------------------
-- 2. givenPreviousModCreateParam_whenAssignParamToBlock_thenThrowIllegalStateException
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 2');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (2, 'Test block 2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Teacher');

INSERT INTO version
(schedule_id)
VALUES
    (2);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (5, 2, 'Mark Robertson');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
(2, 2, 'CREATE_PARAMETER', null, 'Mark Robertson', CURRENT_TIMESTAMP);

--------------------------------------------------
-- 3. givenPreviousModDeleteParamInPreviousVersion_whenAssignParamToBlock_thenCreateModCreateParam
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 3');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (3, 'Test block 3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Form');

INSERT INTO version
(schedule_id)
VALUES
    (3);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (6, 3, 'Remote');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (3, 3, 'DELETE_PARAMETER', 'Stationary', null, CURRENT_TIMESTAMP);

UPDATE version
    SET committed = true
WHERE version.id = 3;

INSERT INTO version
(schedule_id)
VALUES
    (3);

--------------------------------------------------
-- 4. givenPreviousModDeleteParamInCurrentVersionSameValue_whenAssignParamToBlock_thenDeleteMod
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 4');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (4, 'Test block 4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Faculty');

INSERT INTO version
(schedule_id)
VALUES
    (4);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (7, 4, 'Polytechnic');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (5, 4, 'DELETE_PARAMETER', 'Polytechnic', null, CURRENT_TIMESTAMP);

--------------------------------------------------
-- 5. givenPreviousModDeleteParamInCurrentVersionDifferentValue_whenAssignParamToBlock_thenCreateModUpdateParam
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 5');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (4, 'Test block 5', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Group');

INSERT INTO version
(schedule_id)
VALUES
    (5);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (8, 5, '2');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (6, 5, 'DELETE_PARAMETER', '1', null, CURRENT_TIMESTAMP);
