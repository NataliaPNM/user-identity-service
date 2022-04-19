DELETE FROM Users;
ALTER SEQUENCE user_generator RESTART;
INSERT INTO Users (id, phone, name, surname, patronymic, email, role)VALUES (nextval('user_generator'), 89922098031, 'Natalia', 'Ponomareva', 'Aleksandrovna','ttnpnm@yandex.ru','ROLE_USER');
INSERT INTO Users (id, phone, name, surname, patronymic, email, role)VALUES (nextval('user_generator'), 89638776250, 'Tatiana', 'Ponomareva', 'Victorovna','1111@yandex.ru', 'ROLE_USER');

DELETE FROM secure_data;
ALTER SEQUENCE secure_generator RESTART;
INSERT INTO secure_data (id, login, password, user_id)VALUES (nextval('secure_generator'),'postgres','$2a$12$SaJgBAKTMINE/Nf1.bLhG.c1zjgCLJ.l3lHAH8GYQMq.kN3YEbaH.',1);
INSERT INTO secure_data (id, login, password, user_id)VALUES (nextval('secure_generator'),'ttnpnm','$2a$12$yPCb2tQjR.TW6S7xyD69CuDvS7flvmyiyxqqHPOg1lmwBfOtRglJ6',2);

