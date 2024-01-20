---------------------------------------------------------------------------
-- UPDATE PARAMETER WITHIN BLOCK TEST DATA
---------------------------------------------------------------------------

--------------------------------------------------
-- 1. givenNoPreviousMod_whenUpdateParamWithinBlock_thenThrowEntityNotFoundException
--------------------------------------------------
INSERT INTO schedule
    (name)
VALUES
    ('Test schedule 6');

INSERT INTO block
    (schedule_id, name, start_date, end_date)
VALUES
    (6, 'Test block 6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
    (name)
VALUES
    ('State');

INSERT INTO version
    (schedule_id)
VALUES
    (6);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
(9, 6, 'Texas');

--------------------------------------------------
-- 2. givenPreviousModDeleteParam_whenUpdateParamWithinBlock_thenThrowIllegalStateException
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 7');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (7, 'Test block 7', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Country');

INSERT INTO version
(schedule_id)
VALUES
    (7);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (10, 7, 'Poland');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (8, 7, 'DELETE_PARAMETER', 'Poland', null, CURRENT_TIMESTAMP);

--------------------------------------------------
-- 3. givenPreviousModCreateParamInPreviousVersionDifferentValue_whenUpdateParamWithinBlock_thenCreateModUpdateParam
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 8');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (8, 'Test block 8', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Continent');

INSERT INTO version
(schedule_id)
VALUES
    (8);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (11, 8, 'Europe');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (9, 8, 'CREATE_PARAMETER', NULL, 'Asia', CURRENT_TIMESTAMP);

UPDATE version
SET committed = true
WHERE version.id = 9;

INSERT INTO version
(schedule_id)
VALUES
    (8);

--------------------------------------------------
-- 4. givenPreviousModCreateParamInPreviousVersionSameValue_whenUpdateParamWithinBlock_thenDontCreateMod
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 9');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (9, 'Test block 9', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Car');

INSERT INTO version
(schedule_id)
VALUES
    (9);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (12, 9, 'BMW');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (10, 9, 'CREATE_PARAMETER', NULL, 'BMW', CURRENT_TIMESTAMP);

UPDATE version
SET committed = true
WHERE version.id = 11;

INSERT INTO version
(schedule_id)
VALUES
    (9);

--------------------------------------------------
-- 5. givenPreviousModUpdateParamInCurrentVersionSameValue_whenUpdateParamWithinBlock_thenDeleteMod
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 10');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (10, 'Test block 10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Team');

INSERT INTO version
(schedule_id)
VALUES
    (10);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (13, 10, 'Real Madrid');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (13, 10, 'UPDATE_PARAMETER', 'Real Madrid', 'Barcelona', CURRENT_TIMESTAMP);

--------------------------------------------------
-- 6. givenPreviousModUpdateParamInCurrentVersionDifferentValue_whenUpdateParamWithinBlock_thenUpdateModNewValue
--------------------------------------------------
INSERT INTO schedule
(name)
VALUES
    ('Test schedule 11');

INSERT INTO block
(schedule_id, name, start_date, end_date)
VALUES
    (11, 'Test block 11', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parameter_dict
(name)
VALUES
    ('Company');

INSERT INTO version
(schedule_id)
VALUES
    (11);

INSERT INTO block_parameter
(parameter_dict_id, block_id, value)
VALUES
    (14, 11, 'Real Madrid');

INSERT INTO modification
(version_id, block_parameter_id, type, old_value, new_value, timestamp)
VALUES
    (14, 11, 'UPDATE_PARAMETER', 'McDonalds', 'Burger King', CURRENT_TIMESTAMP);
