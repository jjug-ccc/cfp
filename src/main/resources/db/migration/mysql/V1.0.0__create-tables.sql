CREATE TABLE conference (
  conf_id     BINARY(16)   NOT NULL,
  conf_date   DATE         NOT NULL,
  conf_name   VARCHAR(255) NOT NULL,
  conf_status INT(5)       NOT NULL DEFAULT 0,
  created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (conf_id)
)
  ENGINE InnoDB;
CREATE TABLE speaker (
  speaker_id           BINARY(16)   NOT NULL,
  activities           LONGTEXT     NOT NULL,
  bio                  LONGTEXT     NOT NULL,
  company_or_community VARCHAR(255) NOT NULL,
  github               VARCHAR(255) NOT NULL,
  profile_url          VARCHAR(255) NOT NULL,
  name                 VARCHAR(255) NOT NULL,
  created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (speaker_id)
)
  ENGINE InnoDB;
CREATE TABLE submission (
  submission_id     BINARY(16)   NOT NULL,
  category          INT(5)       NOT NULL,
  description       LONGTEXT     NOT NULL,
  language          INT(5)       NOT NULL,
  level             INT(5)       NOT NULL,
  talk_type         INT(5)       NOT NULL,
  submission_status INT(5)       NOT NULL DEFAULT 0,
  target            VARCHAR(255) NOT NULL,
  title             VARCHAR(255) NOT NULL,
  conference_id     BINARY(16)   NOT NULL,
  speaker_id        BINARY(16)   NOT NULL,
  created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (submission_id)
)
  ENGINE InnoDB;
ALTER TABLE speaker
  ADD CONSTRAINT uk_github UNIQUE (github);
ALTER TABLE submission
  ADD CONSTRAINT fk_conf_id FOREIGN KEY (conference_id) REFERENCES conference (conf_id);
ALTER TABLE submission
  ADD CONSTRAINT fk_speaker_id FOREIGN KEY (speaker_id) REFERENCES speaker (speaker_id);
ALTER TABLE submission
  ADD INDEX idx_submission_status(submission_status);

