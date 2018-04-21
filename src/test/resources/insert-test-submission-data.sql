INSERT INTO submission (submission_id, category, description, language, level, talk_type, submission_status, target, title, conference_id)
VALUES (0x00000000000000000000000021000200, 1, 'test-description1', 0, 0, 0, 10, 'テスト1が好きな人', 'テストタイトル1', 0x00000000000000000000000021000101);

INSERT INTO submission (submission_id, category, description, language, level, talk_type, submission_status, target, title, conference_id)
VALUES (0x00000000000000000000000021000201, 1, 'test-description2', 0, 5, 0, 0, 'テスト2が好きな人', 'テストタイトル2', 0x00000000000000000000000021000101);

INSERT INTO submission (submission_id, category, description, language, level, talk_type, submission_status, target, title, conference_id)
VALUES (0x00000000000000000000000021000202, 1, 'test-description3', 0, 10, 0, 5, 'テスト3が好きな人', 'テストタイトル3', 0x00000000000000000000000021000101);

INSERT INTO submission (submission_id, category, description, language, level, talk_type, submission_status, target, title, conference_id)
VALUES (0x00000000000000000000000021000203, 1, 'test-description4', 0, 0, 0, 10, 'テスト4が好きな人', 'テストタイトル4', 0x00000000000000000000000021000101);

INSERT INTO submission (submission_id, category, description, language, level, talk_type, submission_status, target, title, conference_id)
VALUES (0x00000000000000000000000021000204, 1, 'test-description5', 0, 10, 0, 15, 'テスト5が好きな人', 'テストタイトル5', 0x00000000000000000000000021000101);

INSERT INTO submission (submission_id, category, description, language, level, talk_type, submission_status, target, title, conference_id)
VALUES (0x00000000000000000000000021000205, 1, 'test-description6', 0, 10, 0, 20, 'テスト6が好きな人', 'テストタイトル6', 0x00000000000000000000000021000101);


INSERT INTO speaker (speaker_id, bio, company_or_community, github, profile_url, name, email)
VALUES (0x00000000000000000000000021000300, '日本Javaユーザーグループ（Japan Java User Group/JJUG）は6000人以上のJavaエンジニアが参加する日本最大のJavaコミュニティです。', 'JJUG', 'jjug1', 'http://www.java-users.jp/wp-content/uploads/2012/09/duke.png', 'duke', 'jjug1@example.com');
INSERT INTO activity (speaker_id, activity_type, url)
VALUES (0x00000000000000000000000021000300, 1, 'https://twitter.com/JJUG');

INSERT INTO speaker (speaker_id, bio, company_or_community, github, profile_url, name, email)
VALUES (0x00000000000000000000000021000301, '日本Javaユーザーグループ（Japan Java User Group/JJUG）は6000人以上のJavaエンジニアが参加する日本最大のJavaコミュニティです。', 'JJUG', 'jjug2', 'http://www.java-users.jp/wp-content/uploads/2012/09/duke.png', 'duke', 'jjug2@example.com');
INSERT INTO activity (speaker_id, activity_type, url)
VALUES (0x00000000000000000000000021000301, 1, 'https://twitter.com/JJUG');

INSERT INTO speaker (speaker_id, bio, company_or_community, github, profile_url, name, email)
VALUES (0x00000000000000000000000021000302, '日本Javaユーザーグループ（Japan Java User Group/JJUG）は6000人以上のJavaエンジニアが参加する日本最大のJavaコミュニティです。', 'JJUG', 'jjug3', 'http://www.java-users.jp/wp-content/uploads/2012/09/duke.png', 'duke', 'jjug3@example.com');
INSERT INTO activity (speaker_id, activity_type, url)
VALUES (0x00000000000000000000000021000302, 1, 'https://twitter.com/JJUG');

INSERT INTO speaker (speaker_id, bio, company_or_community, github, profile_url, name, email)
VALUES (0x00000000000000000000000021000303, '日本Javaユーザーグループ（Japan Java User Group/JJUG）は6000人以上のJavaエンジニアが参加する日本最大のJavaコミュニティです。', 'JJUG', 'jjug4', 'http://www.java-users.jp/wp-content/uploads/2012/09/duke.png', 'duke', 'jjug4@example.com');
INSERT INTO activity (speaker_id, activity_type, url)
VALUES (0x00000000000000000000000021000303, 1, 'https://twitter.com/JJUG');

INSERT INTO speaker (speaker_id, bio, company_or_community, github, profile_url, name, email)
VALUES (0x00000000000000000000000021000304, '日本Javaユーザーグループ（Japan Java User Group/JJUG）は6000人以上のJavaエンジニアが参加する日本最大のJavaコミュニティです。', 'JJUG', 'jjug5', 'http://www.java-users.jp/wp-content/uploads/2012/09/duke.png', 'duke', 'jjug5@example.com');
INSERT INTO activity (speaker_id, activity_type, url)
VALUES (0x00000000000000000000000021000304, 1, 'https://twitter.com/JJUG');


INSERT INTO submission_speaker (submission_id, speaker_id) VALUES (0x00000000000000000000000021000200, 0x00000000000000000000000021000300);
INSERT INTO submission_speaker (submission_id, speaker_id) VALUES (0x00000000000000000000000021000201, 0x00000000000000000000000021000301);
INSERT INTO submission_speaker (submission_id, speaker_id) VALUES (0x00000000000000000000000021000202, 0x00000000000000000000000021000301);
INSERT INTO submission_speaker (submission_id, speaker_id) VALUES (0x00000000000000000000000021000203, 0x00000000000000000000000021000302);
INSERT INTO submission_speaker (submission_id, speaker_id) VALUES (0x00000000000000000000000021000203, 0x00000000000000000000000021000303);
INSERT INTO submission_speaker (submission_id, speaker_id) VALUES (0x00000000000000000000000021000204, 0x00000000000000000000000021000303);
INSERT INTO submission_speaker (submission_id, speaker_id) VALUES (0x00000000000000000000000021000205, 0x00000000000000000000000021000304);
