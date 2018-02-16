CREATE TABLE sponsored_submission (
  submission_id BINARY(16)   NOT NULL,
  sponsor_id    VARCHAR(255) NOT NULL,
  PRIMARY KEY (submission_id, sponsor_id)
)
  ENGINE InnoDB;

ALTER TABLE sponsored_submission
  ADD CONSTRAINT fk_sponsored_submission_submission FOREIGN KEY (submission_id) REFERENCES submission (submission_id);
ALTER TABLE sponsored_submission
  ADD CONSTRAINT fk_sponsored_submission_sponsor FOREIGN KEY (sponsor_id) REFERENCES sponsor (sponsor_id);