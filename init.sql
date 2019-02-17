CREATE USER webuser WITH PASSWORD 'webpassword';
CREATE DATABASE astartes_db;
GRANT ALL PRIVILEGES ON DATABASE astartes_db TO webuser;
\connect astartes_db webuser;
CREATE TABLE astartes
(
  id        BIGSERIAL PRIMARY KEY,
  name      VARCHAR(255),
  title     VARCHAR(255),
  position  VARCHAR(255),
  planet    VARCHAR(255),
  birthdate TIMESTAMP
);

INSERT INTO astartes (name, title, position, planet, birthdate)
VALUES ('Абаддон', 'Командор', 'Советник Хоруса', 'Хтония', '1000.01.01');
INSERT INTO astartes (name, title, position, planet, birthdate)
VALUES ('Калгар', 'Лорд', 'Магистр ордена Ультрамаринов', 'Ультрамар', '999.01.01');
INSERT INTO astartes (name, title, position, planet, birthdate)
VALUES ('Шрайк', 'Командор', 'Магистр Гвардии Ворона', 'Киавар', '1001.01.01');