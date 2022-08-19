DELETE FROM address;
INSERT INTO address (address_id, country, city, street, postal_code, house, flat, block)VALUES ('0854f660-5e91-4e95-a466-75fc5ac2f22c', 'Russia', 'Perm','Pushkina',123456,115,82,null);
INSERT INTO address (address_id, country, city, street, postal_code, house, flat, block)VALUES ('00167fd4-41ea-4ae4-8d4c-e0371482fd8a', 'Ukraine', 'Kiev','Bolshaya',654321,100,1,3);
INSERT INTO address (address_id, country, city, street, postal_code, house, flat, block)VALUES ('aaa121ff-44ab-450f-b2fb-b4960b04df58', 'Belarus', 'Minsk','Lenina',918273,14,12,null);
INSERT INTO address (address_id, country, city, street, postal_code, house, flat, block)VALUES ('91f3819b-7bd1-4c00-aa20-052f113fabff', 'Russia', 'Moscow','Lenina',123456,115,82,null);

DELETE FROM passport;
INSERT INTO passport (passport_id, date_of_birth, serial_number, department_code, department_issued, address_id)VALUES ('74ce3f2a-f20a-430c-8ef5-1d835ed67fcf', '14.08.1999','1234 12345',918,'PKM','00167fd4-41ea-4ae4-8d4c-e0371482fd8a');
INSERT INTO passport (passport_id, date_of_birth, serial_number, department_code, department_issued, address_id)VALUES ('6d600361-4d85-4dad-9888-bbad43352813', '14.08.1999','3214 94856',542,'PKM','0854f660-5e91-4e95-a466-75fc5ac2f22c');
INSERT INTO passport (passport_id, date_of_birth, serial_number, department_code, department_issued, address_id)VALUES ('40d3674b-120e-48b4-b738-113fc79a4a08', '14.08.1999','4758 10985',123,'PKM','aaa121ff-44ab-450f-b2fb-b4960b04df58');

DELETE FROM person;
INSERT INTO person (person_id, phone, name, surname, patronymic, email, role,address_id, passport_id)VALUES ('d6d83746-d862-4562-99d4-4ec5a664a59a',  79922098031, 'Natalia', 'Ponomareva', 'Aleksandrovna', 'ttnpnm@yandex.ru', 'ROLE_USER','0854f660-5e91-4e95-a466-75fc5ac2f22c','74ce3f2a-f20a-430c-8ef5-1d835ed67fcf');
INSERT INTO person (person_id, phone, name, surname, patronymic, email, role,address_id, passport_id)VALUES ('f5bffea0-c09e-4f92-9722-1c91ed0a7d2a',  79638776250, 'Tatiana', 'Ponomareva', 'Victorovna', 'Smokdog@mac.com', 'ROLE_USER','0854f660-5e91-4e95-a466-75fc5ac2f22c','6d600361-4d85-4dad-9888-bbad43352813');
INSERT INTO person (person_id, phone, name, surname, patronymic, email, role,address_id, passport_id)VALUES ('39002758-de86-4f0c-afbd-2ed7eade6b39',  79638725951, 'Tatyana', 'Mamykina', 'Igorevna', 'tanya_mamykina92@inbox.ru',  'ROLE_USER','00167fd4-41ea-4ae4-8d4c-e0371482fd8a','40d3674b-120e-48b4-b738-113fc79a4a08');

DELETE FROM notification_settings;
INSERT INTO notification_settings(notification_settings_id, default_type_of_confirmation, confirmation_lock, email_lock, email_lock_time, push_lock, push_lock_time, person_id) VALUES ('1053cd94-b07e-42fe-85e4-3bebd8370c1c','email','NONE', false,'', false,'','d6d83746-d862-4562-99d4-4ec5a664a59a');
INSERT INTO notification_settings(notification_settings_id, default_type_of_confirmation, confirmation_lock, email_lock, email_lock_time, push_lock, push_lock_time, person_id)  VALUES ('2dd49794-8694-42e2-8193-75760cad693d','email','NONE', false,'', false,'','f5bffea0-c09e-4f92-9722-1c91ed0a7d2a');
INSERT INTO notification_settings(notification_settings_id, default_type_of_confirmation, confirmation_lock, email_lock, email_lock_time, push_lock, push_lock_time, person_id) VALUES ('bb68984f-9514-4aba-9b43-38aef0ffadcf','email','NONE', false,'', false,'','39002758-de86-4f0c-afbd-2ed7eade6b39');

DELETE FROM credentials;
INSERT INTO credentials (credentials_id, lock, lock_time, login, password, is_account_verified, temporary_password, refresh_token, person_id)VALUES ('522be286-9ae7-468b-9857-3e8586f1a6d2',false, '', 'postgres','$2a$12$SaJgBAKTMINE/Nf1.bLhG.c1zjgCLJ.l3lHAH8GYQMq.kN3YEbaH.',false,'','','d6d83746-d862-4562-99d4-4ec5a664a59a');
INSERT INTO credentials (credentials_id, lock, lock_time, login, password, is_account_verified, temporary_password, refresh_token, person_id)VALUES ('a0019849-9e82-49d6-b314-f3139f4678c2',false, '', 'ttnpnm','$2a$12$yPCb2tQjR.TW6S7xyD69CuDvS7flvmyiyxqqHPOg1lmwBfOtRglJ6',true,'','','f5bffea0-c09e-4f92-9722-1c91ed0a7d2a');
INSERT INTO credentials (credentials_id, lock, lock_time, login, password, is_account_verified, temporary_password, refresh_token, person_id)VALUES ('1eeae233-278d-4a79-9745-ca45832896fd',false, '', '11175869705452312345','$2a$12$QNFNTXqkjyB/kxBP40IpROKgCFdaTN.uVp2vGHhtEMvYKHk9UBGLW',true,'','','39002758-de86-4f0c-afbd-2ed7eade6b39');

