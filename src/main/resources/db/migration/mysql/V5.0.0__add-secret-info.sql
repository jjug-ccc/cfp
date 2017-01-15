ALTER TABLE speaker
  ADD email VARCHAR(255) NOT NULL DEFAULT 'foo@example.com';
ALTER TABLE speaker
  ADD transportation_allowance BIT(1) NOT NULL DEFAULT FALSE;
ALTER TABLE speaker
  ADD city VARCHAR(255);
ALTER TABLE speaker
  ADD note LONGTEXT;