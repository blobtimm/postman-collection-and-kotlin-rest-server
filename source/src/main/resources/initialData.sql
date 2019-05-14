CREATE TABLE person (
  id            IDENTITY PRIMARY KEY,
  first_name    VARCHAR,
  last_name     VARCHAR,
  username      VARCHAR,
  date_created  DATE
);

INSERT INTO person (first_name, last_name, username, date_created) VALUES ('Moritz', 'Schulze', 'mshulze', '2001-01-22');
INSERT INTO person (first_name, last_name, username, date_created) VALUES ('Alexander', 'Hanschke', 'alex01', '2018-10-01');
INSERT INTO person (first_name, last_name, username, date_created) VALUES ('Adrian', 'Krion', 'anon', '2012-11-23');
