CREATE TABLE vote (
  vote_id       BINARY(16)   NOT NULL,
  submission_id BINARY(16)   NOT NULL,
  github        VARCHAR(255) NOT NULL,
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (vote_id)
)
  ENGINE InnoDB;

ALTER TABLE vote
  ADD CONSTRAINT uk_github_submission UNIQUE (github, submission_id);
ALTER TABLE vote
  ADD INDEX idx_submission(submission_id);
