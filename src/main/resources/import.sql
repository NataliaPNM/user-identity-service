DELETE FROM notification_settings;
INSERT INTO notification_settings(notification_settings_id, default_type_of_confirmation, confirmation_lock, email_lock, email_lock_time, push_lock, push_lock_time) VALUES ('1053cd94-b07e-42fe-85e4-3bebd8370c1c','email','NONE', false,'', false,'');
INSERT INTO notification_settings(notification_settings_id, default_type_of_confirmation, confirmation_lock, email_lock, email_lock_time, push_lock, push_lock_time)  VALUES ('2dd49794-8694-42e2-8193-75760cad693d','email','NONE', false,'', false,'');
INSERT INTO notification_settings(notification_settings_id, default_type_of_confirmation, confirmation_lock, email_lock, email_lock_time, push_lock, push_lock_time) VALUES ('bb68984f-9514-4aba-9b43-38aef0ffadcf','email','NONE', false,'', false,'');

DELETE FROM person;
INSERT INTO person (person_id, phone, name, surname, patronymic, notification_settings_id, email, role)VALUES ('d6d83746-d862-4562-99d4-4ec5a664a59a',  79922098031, 'Natalia', 'Ponomareva', 'Aleksandrovna', '1053cd94-b07e-42fe-85e4-3bebd8370c1c', 'mihant91@gmail.com', 'ROLE_USER');
INSERT INTO person (person_id, phone, name, surname, patronymic, notification_settings_id, email, role)VALUES ('f5bffea0-c09e-4f92-9722-1c91ed0a7d2a',  79638776250, 'Tatiana', 'Ponomareva', 'Victorovna', '2dd49794-8694-42e2-8193-75760cad693d', '1111@yandex.ru', 'ROLE_USER');
INSERT INTO person (person_id, phone, name, surname, patronymic, notification_settings_id, email, role)VALUES ('39002758-de86-4f0c-afbd-2ed7eade6b39',  79638725951, 'Tatyana', 'Mamykina', 'Igorevna', 'bb68984f-9514-4aba-9b43-38aef0ffadcf','tanya_mamykina92@inbox.ru',  'ROLE_USER');

DELETE FROM credentials;
INSERT INTO credentials (credentials_id, lock, lock_time, login, password, is_account_verified, temporary_password, refresh_token, person_id)VALUES ('522be286-9ae7-468b-9857-3e8586f1a6d2',false, '', 'postgres','$2a$12$SaJgBAKTMINE/Nf1.bLhG.c1zjgCLJ.l3lHAH8GYQMq.kN3YEbaH.',false,'','','d6d83746-d862-4562-99d4-4ec5a664a59a');
INSERT INTO credentials (credentials_id, lock, lock_time, login, password, is_account_verified, temporary_password, refresh_token, person_id)VALUES ('a0019849-9e82-49d6-b314-f3139f4678c2',false, '', 'ttnpnm','$2a$12$yPCb2tQjR.TW6S7xyD69CuDvS7flvmyiyxqqHPOg1lmwBfOtRglJ6',false,'','','f5bffea0-c09e-4f92-9722-1c91ed0a7d2a');
INSERT INTO credentials (credentials_id, lock, lock_time, login, password, is_account_verified, temporary_password, refresh_token, person_id)VALUES ('1eeae233-278d-4a79-9745-ca45832896fd',false, '', '11175869705452312345','$2a$12$QNFNTXqkjyB/kxBP40IpROKgCFdaTN.uVp2vGHhtEMvYKHk9UBGLW',true,'','','39002758-de86-4f0c-afbd-2ed7eade6b39');

