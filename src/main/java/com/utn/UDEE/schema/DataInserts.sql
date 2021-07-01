select * from measurements;
select * from addresses;
select * from invoices;
select * from meters;
select * from models;
select * from users;
select * from rates;
select * from brands;

INSERT INTO brands(`name`) VALUES
("BOIERO S.A"),
("Compañia Argentina De Medidores S.A.");

INSERT INTO models(`brand_id`,`name`) VALUES
(1,"BOER"),
(1,"IERO"),
(2, "CAMSA");

INSERT INTO meters(`model_id`,`password`) VALUES
(1,"123456"),
(1,"1234567"),
(2,"1234568")
;

INSERT INTO users(`name`, `last_name`, `username`, `password`,`email`, `type_user`) VALUES
("Leo", "Messi", "Messias", "qatar2022","Messi@gmail.com", 1),
("Angel", "Di Maria", "Centritos", "SinQuebrar","Angelito@hotmail.com", 1),
("Sergio", "Aguero", "SLAKUN10", "Streamer","AgueroS@outlook.com", 1),
("admin", "udee", "adminUDEE", "123456","admin@admin.com", 2)
;

INSERT INTO `rates`(`value`,`type_rate`) VALUES
(100,"A"),
(200,"B")
;

INSERT INTO `addresses`(`meter_id`,`user_id`,`id_rate`,street,number,apartment) VALUES
(1,1,1,"San Lorenzo",2371,""),
(2,1,2,"Independencia",1456,"6°A"),
(3,3,2,"Santiago Del Estero",1233,"3°B")
;



INSERT INTO `measurements` (`meter_id`,`invoice_id`,`date`,`quantity_kw`,`price_measurement`) VALUES
(1,NULL,NOW(),0,7),
(1,NULL,DATE_ADD(NOW(),INTERVAL 5 MINUTE),2,1),
(1,NULL,DATE_ADD(NOW(),INTERVAL 10 MINUTE),5,5),     
(1,NULL,DATE_ADD(NOW(),INTERVAL 15 MINUTE),9,8),     
(1,NULL,DATE_ADD(NOW(),INTERVAL 20 MINUTE),15,12),    
(1,NULL,DATE_ADD(NOW(),INTERVAL 25 MINUTE),17,13),
(1,NULL,DATE_ADD(NOW(),INTERVAL 30 MINUTE),18,16),
(1,NULL,DATE_ADD(NOW(),INTERVAL 35 MINUTE),24,18),
(2,NULL,NOW(),0,2),
(2,NULL,DATE_ADD(NOW(),INTERVAL 5 MINUTE),8,3),
(2,NULL,DATE_ADD(NOW(),INTERVAL 10 MINUTE),15,4),
(2,NULL,DATE_ADD(NOW(),INTERVAL 15 MINUTE),20,5),
(2,NULL,DATE_ADD(NOW(),INTERVAL 20 MINUTE),26,6),
(2,NULL,DATE_ADD(NOW(),INTERVAL 25 MINUTE),30,7),
(2,NULL,DATE_ADD(NOW(),INTERVAL 30 MINUTE),31,8),
(2,NULL,DATE_ADD(NOW(),INTERVAL 35 MINUTE),34,9),
(3,NULL,NOW(),0,20),
(3,NULL,DATE_ADD(NOW(),INTERVAL 5 MINUTE),8,25),
(3,NULL,DATE_ADD(NOW(),INTERVAL 10 MINUTE),15,30),
(3,NULL,DATE_ADD(NOW(),INTERVAL 15 MINUTE),24,32),
(3,NULL,DATE_ADD(NOW(),INTERVAL 20 MINUTE),30,36),
(3,NULL,DATE_ADD(NOW(),INTERVAL 25 MINUTE),35,38),
(3,NULL,DATE_ADD(NOW(),INTERVAL 30 MINUTE),38,40),
(3,NULL,DATE_ADD(NOW(),INTERVAL 35 MINUTE),40,50);