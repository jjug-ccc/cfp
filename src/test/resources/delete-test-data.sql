DELETE FROM submission_speaker WHERE submission_id IN (SELECT submission_id FROM submission WHERE conference_id IN (SELECT conf_id FROM conference WHERE conf_name LIKE 'Test %'));
DELETE FROM submission WHERE conference_id IN (SELECT conf_id FROM conference WHERE conf_name LIKE 'Test %');
DELETE FROM conference WHERE conf_name LIKE 'Test %';