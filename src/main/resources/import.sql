DELETE FROM person;
INSERT INTO person (person_id, phone, name, surname, patronymic, email, role)VALUES ('f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454', 79922098031, 'Natalia', 'Ponomareva', 'Aleksandrovna','mihant91@gmail.com','ROLE_USER');
INSERT INTO person (person_id, phone, name, surname, patronymic, email, role)VALUES ('14bca33b-52b9-4010-8f92-67ba346d72cb', 79638776250, 'Tatiana', 'Ponomareva', 'Victorovna','1111@yandex.ru', 'ROLE_USER');

DELETE FROM credentials;
INSERT INTO credentials (credentials_id, login, password, refresh_token, person_id)VALUES ('fdca1466-451f-4bf5-8952-35615f19157e', 'postgres','$2a$12$SaJgBAKTMINE/Nf1.bLhG.c1zjgCLJ.l3lHAH8GYQMq.kN3YEbaH.','','f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454');
INSERT INTO credentials (credentials_id, login, password, refresh_token, person_id)VALUES ('9aa170b8-69cd-4358-b37c-d03c38440325', 'ttnpnm','$2a$12$yPCb2tQjR.TW6S7xyD69CuDvS7flvmyiyxqqHPOg1lmwBfOtRglJ6','','14bca33b-52b9-4010-8f92-67ba346d72cb');

