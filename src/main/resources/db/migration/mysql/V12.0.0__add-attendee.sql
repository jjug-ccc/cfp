CREATE TABLE attendee (
  attendee_id BINARY(16)   NOT NULL,
  conf_id     BINARY(16)   NOT NULL,
  email       VARCHAR(255) NOT NULL,
  PRIMARY KEY (attendee_id)
);

CREATE TABLE submission_attendee (
  submission_id BINARY(16) NOT NULL,
  attendee_id   BINARY(16) NOT NULL,
  PRIMARY KEY (submission_id, attendee_id)
);

ALTER TABLE submission_attendee
  ADD CONSTRAINT submission_attendee_fk_attendee FOREIGN KEY (attendee_id)
REFERENCES attendee (attendee_id);

ALTER TABLE submission_attendee
  ADD CONSTRAINT submission_attendee_fk_submission FOREIGN KEY (submission_id)
REFERENCES submission (submission_id);

ALTER TABLE attendee
  ADD UNIQUE unq_attendee_email_conf_id(conf_id, email);

ALTER TABLE attendee
  ADD INDEX idx_attendee_email(email);