CREATE TABLE sponsor (
  sponsor_id       VARCHAR(255) NOT NULL,
  sponsor_name     VARCHAR(255) NOT NULL,
  sponsor_type     INT(5)       NOT NULL,
  sponsor_logo_url VARCHAR(255) NOT NULL,
  sponsor_url      VARCHAR(255) NOT NULL,
  conference_id    BINARY(16)   NOT NULL,
  PRIMARY KEY (sponsor_id)
)
  ENGINE InnoDB;

CREATE TABLE sponsor_credential (
  sponsor_id VARCHAR(255) NOT NULL,
  credential VARCHAR(255) NOT NULL,
  PRIMARY KEY (sponsor_id)
)
  ENGINE InnoDB;

ALTER TABLE sponsor_credential
  ADD CONSTRAINT fk_sponsor_credential_sponsor FOREIGN KEY (sponsor_id) REFERENCES sponsor (sponsor_id);
ALTER TABLE sponsor
  ADD CONSTRAINT fk_conference_sponsor_conf_id FOREIGN KEY (conference_id) REFERENCES conference (conf_id);