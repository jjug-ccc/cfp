CREATE TABLE activity (
  speaker_id           BINARY(16) NOT NULL,
  activity_type           INT(5) NOT NULL,
  url           VARCHAR(255) NOT NULL
)
  ENGINE InnoDB;

ALTER TABLE activity ADD CONSTRAINT fk_activity_speaker_id FOREIGN KEY (speaker_id) REFERENCES speaker (speaker_id);

ALTER TABLE speaker MODIFY COLUMN activities LONGTEXT;
ALTER TABLE activity
  ADD CONSTRAINT uk_activity_speaker UNIQUE (speaker_id, activity_type, url);
