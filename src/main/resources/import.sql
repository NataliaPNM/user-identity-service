DELETE FROM person;
INSERT INTO person (person_id, phone, name, surname, patronymic, email, role)VALUES ('d6d83746-d862-4562-99d4-4ec5a664a59a', 79922098031, 'Natalia', 'Ponomareva', 'Aleksandrovna','mihant91@gmail.com','ROLE_USER');
INSERT INTO person (person_id, phone, name, surname, patronymic, email, role)VALUES ('f5bffea0-c09e-4f92-9722-1c91ed0a7d2a', 79638776250, 'Tatiana', 'Ponomareva', 'Victorovna','1111@yandex.ru', 'ROLE_USER');

DELETE FROM credentials;
INSERT INTO credentials (credentials_id, login, password, refresh_token, person_id)VALUES ('522be286-9ae7-468b-9857-3e8586f1a6d2', 'postgres','$2a$12$SaJgBAKTMINE/Nf1.bLhG.c1zjgCLJ.l3lHAH8GYQMq.kN3YEbaH.','','d6d83746-d862-4562-99d4-4ec5a664a59a');
INSERT INTO credentials (credentials_id, login, password, refresh_token, person_id)VALUES ('a0019849-9e82-49d6-b314-f3139f4678c2', 'ttnpnm','$2a$12$yPCb2tQjR.TW6S7xyD69CuDvS7flvmyiyxqqHPOg1lmwBfOtRglJ6','','f5bffea0-c09e-4f92-9722-1c91ed0a7d2a');

