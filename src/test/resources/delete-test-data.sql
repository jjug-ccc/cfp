DELETE FROM sponsor_credential_reset WHERE sponsor_id IN (SELECT sponsor_id FROM sponsor WHERE conference_id IN (SELECT conf_id FROM conference WHERE conf_name LIKE 'Test %'));
DELETE FROM sponsor_credential WHERE sponsor_id IN (SELECT sponsor_id FROM sponsor WHERE conference_id IN (SELECT conf_id FROM conference WHERE conf_name LIKE 'Test %'));
DELETE FROM sponsor WHERE conference_id IN (SELECT conf_id FROM conference WHERE conf_name LIKE 'Test %');
DELETE FROM submission_speaker WHERE submission_id IN (SELECT submission_id FROM submission WHERE conference_id IN (SELECT conf_id FROM conference WHERE conf_name LIKE 'Test %'));
DELETE FROM submission WHERE conference_id IN (SELECT conf_id FROM conference WHERE conf_name LIKE 'Test %');
DELETE FROM conference WHERE conf_name LIKE 'Test %';