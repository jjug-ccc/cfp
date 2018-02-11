CREATE TABLE sponsor_credential_reset (
  reset_id   BINARY(16)   NOT NULL,
  sponsor_id VARCHAR(255) NOT NULL,
  created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (reset_id)
)
  ENGINE InnoDB;

ALTER TABLE sponsor_credential_reset
  ADD CONSTRAINT fk_sponsor_credential_reset_sponsor FOREIGN KEY (sponsor_id) REFERENCES sponsor (sponsor_id);