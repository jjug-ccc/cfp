INSERT INTO conference (conf_id, conf_date, conf_name, conf_status, conf_cfp_note, conf_vote_note)
VALUES (0x00000000000000000000000021000101, '2100-01-01', 'Test Conf 1', 5 /* CFP */,
        '**テストデータ1**',
        '**テストです！**');

INSERT INTO sponsor (sponsor_id, sponsor_name, sponsor_type, sponsor_logo_url, sponsor_url, conference_id)
VALUES
  ('test-sponsor', 'テストスポンサー', 5, 'https://example.com', 'https://example.com', 0x00000000000000000000000021000101);

INSERT INTO sponsor_credential (sponsor_id, credential)
VALUES ('test-sponsor', '$2a$10$o3.Wy51NB172InfD8o/BR.JeQUqJEmky10gLPH7tyCNf6FJMP3kpC');