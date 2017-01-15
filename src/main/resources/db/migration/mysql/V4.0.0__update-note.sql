ALTER TABLE conference
  ADD conf_cfp_note LONGTEXT NOT NULL;
ALTER TABLE conference
  ADD conf_vote_note LONGTEXT NOT NULL;

ALTER TABLE conference
  DROP conf_note;

UPDATE conference
SET conf_cfp_note = 'Note for CFP', conf_vote_note = 'Note for Vote';