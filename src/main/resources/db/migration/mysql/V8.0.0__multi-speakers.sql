CREATE TABLE submission_speaker (
  submission_id           BINARY(16)   NOT NULL,
  speaker_id           BINARY(16)   NOT NULL
)
  ENGINE InnoDB;

ALTER TABLE submission_speaker
  ADD CONSTRAINT uk_submission_speaker UNIQUE (submission_id, speaker_id);
ALTER TABLE submission_speaker
  ADD CONSTRAINT fk_submission_speaker_submission_id FOREIGN KEY (submission_id) REFERENCES submission (submission_id);
ALTER TABLE submission_speaker
  ADD CONSTRAINT fk_submission_speaker_speaker_id FOREIGN KEY (speaker_id) REFERENCES speaker (speaker_id);

INSERT INTO submission_speaker (SELECT submission_id, speaker_id FROM submission);

ALTER TABLE submission drop foreign key fk_speaker_id;
ALTER TABLE submission DROP INDEX fk_speaker_id;
ALTER TABLE submission DROP COLUMN speaker_id;
