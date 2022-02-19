DROP DATABASE IF EXISTS weatherpi;
DROP USER IF EXISTS weatherpi@localhost;
DROP USER IF EXISTS java;

CREATE DATABASE weatherpi;
USE weatherpi;

CREATE TABLE IF NOT EXISTS arduino
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS monitoring
(
    arduino     INT NOT NULL PRIMARY KEY,
    temperature DEC(4, 2),
    humidity    DEC(4, 2),
    FOREIGN KEY monitoring (arduino) references arduino (id)
);

CREATE OR REPLACE VIEW v_monitoring AS
SELECT id, location, m.temperature, m.humidity
FROM monitoring m
         JOIN arduino a on m.arduino = a.id;

INSERT INTO arduino (location)
VALUES ('Location 1'),
       ('Location 2'),
       ('Location 3'),
       ('Location 4'),
       ('Location 5');

INSERT INTO monitoring (arduino, temperature, humidity)
VALUES (1, 0, 0),
       (2, 0, 0),
       (3, 0, 0),
       (4, 0, 0),
       (5, 0, 0);

CREATE USER weatherpi@localhost IDENTIFIED BY 'G1M1RU';
CREATE USER java IDENTIFIED BY 'G1M1RU';

GRANT SELECT, UPDATE ON weatherpi.monitoring TO weatherpi@localhost;
GRANT SELECT, UPDATE ON weatherpi.arduino TO weatherpi@localhost;
GRANT SELECT ON weatherpi.v_monitoring TO weatherpi@localhost;
GRANT SELECT ON weatherpi.v_monitoring TO java;
