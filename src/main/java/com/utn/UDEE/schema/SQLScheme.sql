CREATE DATABASE IF NOT EXISTS UDEE;
Drop database UDEE;
USE UDEE;
CREATE TABLE IF NOT EXISTS brands(
                                     brand_id INT NOT NULL AUTO_INCREMENT,
                                     `name` VARCHAR(50) NOT NULL,
                                     CONSTRAINT pk_brand PRIMARY KEY (brand_id)
);

CREATE TABLE IF NOT EXISTS models(
                                     model_id INT NOT NULL AUTO_INCREMENT,
                                     brand_id INT NOT NULL,
                                     `name` VARCHAR(50) NOT NULL,
                                     CONSTRAINT pk_model PRIMARY KEY (model_id),
                                     CONSTRAINT fk_model_brand FOREIGN KEY (brand_id) REFERENCES brands(brand_id)
);

CREATE TABLE IF NOT EXISTS meters (
                                      serial_number int NOT NULL AUTO_INCREMENT,
                                      model_id INT,
                                      `password` VARCHAR(30) NOT NULL,
                                      CONSTRAINT pk_meter PRIMARY KEY (serial_number),
                                      CONSTRAINT fk_meter_model FOREIGN KEY (model_id) REFERENCES models(model_id)
);

CREATE TABLE IF NOT EXISTS users(
                                    user_id INT NOT NULL AUTO_INCREMENT,
                                    `name` VARCHAR(50) NOT NULL,
                                    last_name VARCHAR(50) NOT NULL,
                                    username VARCHAR(50) NOT NULL,
                                    email VARCHAR(50) NOT NULL,
                                    `password` VARCHAR(30) NOT NULL,
                                    type_user INT NOT NULL DEFAULT 0,
                                    CONSTRAINT pk_user PRIMARY KEY (user_id),
                                    CONSTRAINT unq_username UNIQUE (username),
                                    CONSTRAINT check_type_user check (type_user BETWEEN 0 AND 1)
);

CREATE TABLE IF NOT EXISTS rates(
                                    rate_id INT NOT NULL AUTO_INCREMENT,
                                    `value` DOUBLE NOT NULL,
                                    type_rate VARCHAR(50) NOT NULL,
                                    CONSTRAINT pk_rate PRIMARY KEY (rate_id)
);

CREATE TABLE IF NOT EXISTS addresses(
                                        address_id INT NOT NULL AUTO_INCREMENT,
                                        meter_id INT,
                                        user_id INT,
                                        id_rate INT,
                                        street VARCHAR(40) NOT NULL,
                                        number INT NOT NULL,
                                        apartment VARCHAR(10),
                                        CONSTRAINT pk_address PRIMARY KEY (address_id),
                                        CONSTRAINT fk_address_meter FOREIGN KEY (meter_id) REFERENCES meters(meter_id),
                                        CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                                        CONSTRAINT fk_address_rate FOREIGN KEY (id_rate) REFERENCES rates(rate_id)
);


CREATE TABLE IF NOT EXISTS invoices(
	invoice_id INT NOT NULL AUTO_INCREMENT,
	address_id INT NOT NULL,
	meter_id INT NOT NULL,
	user_id INT NOT NULL,
	initial_measurement DATETIME NOT NULL,
	final_measurement DATETIME NOT NULL,
	total_consumption DOUBLE NOT NULL,
	Invoice_value DOUBLE,
	expiration DATETIME DEFAULT NOW(),
	`date` DATETIME DEFAULT NOW(),
	payed BOOL DEFAULT FALSE,
	CONSTRAINT pk_bill PRIMARY KEY (invoice_id),
	CONSTRAINT fk_bill_address FOREIGN KEY (address_id) REFERENCES addresses(address_id),
	CONSTRAINT fk_bil_meter FOREIGN KEY (meter_id) REFERENCES meters(meter_id),
	CONSTRAINT fk_bill_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS measurements(
	measurement_id INT NOT NULL AUTO_INCREMENT,
	meter_id  INT NOT NULL,
	Invoice_id  INT,
	date DATETIME,
	quantity_kw DOUBLE NOT NULL,
	price_measurement DOUBLE DEFAULT 0,
	CONSTRAINT pk_measurement PRIMARY KEY (measurement_id),
	CONSTRAINT fk_measurement_meter FOREIGN KEY (meter_id) REFERENCES meters(meter_id),
	CONSTRAINT fk_measurement_bill FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id)
);

/*PUNTO 1
1) Generar las estructuras necesarias para dar soporte a 4 sistemas diferentes :
a) BACKOFFICE, que permitirá el manejo de clientes, medidores y tarifas.
b) CLIENTES, que permitirá consultas de mediciones y facturación.
c) MEDIDORES, que será el sistema que enviará la información de
mediciones a la base de datos.
d) FACTURACIÓN , proceso automático de facturación.
*/

#/------------------------------BACKOFFICE---------------------------------/
CREATE USER 'backoffice'@'Localhost' IDENTIFIED BY 'backofficeRKT';

GRANT SELECT,INSERT,UPDATE,DELETE ON udee.rates TO 'backoffice'@'Localhost';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.meters TO 'backoffice'@'Localhost';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.users TO 'backoffice'@'Localhost';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.addresses TO 'backoffice'@'Localhost';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.models TO 'backoffice'@'Localhost';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.brands TO 'backoffice'@'Localhost';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.rates TO 'backoffice'@'Localhost';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.invoices TO 'backoffice'@'Localhost';
#/------------------------------CLIENTS------------------------------/
CREATE USER 'clients'@'Localhost' IDENTIFIED BY 'clientsRKT';

GRANT SELECT ON udee.measurements TO 'clients'@'Localhost';
GRANT SELECT ON udee.invoices TO 'clients'@'Localhost';

#/------------------------------METERS------------------------------/
CREATE USER 'meters'@'Localhost' IDENTIFIED BY 'metersRKT';

GRANT INSERT ON udee.measurements TO 'meters'@'Localhost';

#/------------------------------BILLING------------------------------/
CREATE USER 'invoice'@'Localhost' IDENTIFIED BY 'invoicingRKT';

GRANT EXECUTE ON PROCEDURE p_all_clients_invoice TO invoice;
GRANT EXECUTE ON PROCEDURE p_invoicing_update_rate TO invoice;

/*2) La facturación se realizará por un proceso automático en la base de datos. Se
debe programar este proceso para el primer día de cada mes y debe generar una
factura por medidor y debe tomar en cuenta todas las mediciones no facturadas
para cada uno de los medidores, sin tener en cuenta su fecha. La fecha de vencimiento de
esta factura será estipulado a 15 días.*/

#SP para generar la facturacion de las mediciones
drop procedure if exists p_all_clients_invoice;
DELIMITER $$
CREATE PROCEDURE p_all_clients_invoice()
BEGIN
    DECLARE vclientId INT;
    DECLARE vserialNumber INT;
    DECLARE vaddressId INT;
    DECLARE vinvoiceId INT;
    DECLARE vInitMeasur DATETIME;
    DECLARE vFinalMeasur DATETIME;
    DECLARE vConsumption DOUBLE;
    DECLARE vinvoiceValue DOUBLE;
    DECLARE vcreated INT DEFAULT NULL;
    DECLARE vFinished INT DEFAULT 0;

    #Cursor creado para despues manejar los datos
    DECLARE Cur_liquidate CURSOR FOR SELECT u.user_id,
											me.serial_number,
                                            a.address_id,
                                            measurements.invoice_id,
                                            MIN(measurements.date) AS `initial Measurement`,
                                            MAX(measurements.date) AS `final Measurement`,
                                            MAX(measurements.quantity_kw) - MIN(measurements.quantity_kw) AS `total Consumption`,
                                            ( MAX(measurements.quantity_kw) - MIN(measurements.quantity_kw) ) * r.value AS `Invoice Value`
											FROM
											 measurements
											 INNER JOIN meters me
												ON measurements.meter_id = me.serial_number
											 INNER JOIN addresses a
												ON a.meter_id = me.serial_number
											 INNER JOIN users u
												ON u.user_id = a.user_id
											 INNER JOIN rates r
												ON a.id_rate = r.rate_id
														 GROUP BY u.user_id, me.serial_number, a.address_id, measurements.invoice_id
											HAVING measurements.invoice_id IS NULL;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET vFinished = 1;

    OPEN cur_liquidate;
    FETCH cur_liquidate INTO vclientId, vserialNumber,vaddressId,vinvoiceId,vInitMeasur,vFinalMeasur,vConsumption,vinvoiceValue;

    WHILE (vFinished = 0) DO
		#Usamos transaction para evitar complicaciones imprevistas
        START TRANSACTION;
			#Creacion de la factura
			INSERT INTO invoices (address_id,meter_id,user_id,initial_measurement,final_measurement,total_consumption,Invoice_value,expiration) VALUES
				(vaddressId,vserialNumber,vClientId,vInitMeasur,vFinalMeasur,vConsumption,vinvoiceValue,DATE_ADD(NOW(),INTERVAL 15 day));

            SET vcreated = LAST_INSERT_ID();
			#Agregando la factura a las mediciones
            UPDATE measurements SET InvoiceId = vCreated WHERE meter_id = vserialNumber AND `date` BETWEEN vInitMeasur AND vFinalMeasur;

        COMMIT;

		FETCH cur_liquidate INTO vclientId, vserialNumber,vaddressId,vinvoiceId,vInitMeasur,vFinalMeasur,vConsumption,vinvoiceValue ;
	END WHILE;

    CLOSE cur_liquidate;
END
$$
DELIMITER ;
#Generado evento para ejecucion mensual
SET GLOBAL event_scheduler = ON;

CREATE EVENT if not exists e_liquidate_all_clients
ON SCHEDULE EVERY 1 MONTH STARTS '2021-07-01 00:00:00'
DO CALL p_all_clients_invoice();

/*3) Generar las estructuras necesarias para el cálculo de precio de cada medición y las
inserción de la misma. Se debe tener en cuenta que una modificación en la tarifa debe.
modificar el precio de cada una de estas mediciones en la base de datos y generar una
factura de ajuste a la nueva medición de cada una de las mediciones involucradas con esta
tarifa*/

#TRIGGERS
delimiter //
create trigger	TBI_add_price_measurement before insert on measurements for each row
begin
	declare vValueRate float default null;
    declare vlastDate datetime default null;
    declare vKwMeasure float default null;

    select max(`date`) into vlastDate from measurements where meter_id = new.meter_id and `date` <=new.date;
    select rates.`value`  into vValueRate from rates inner join addresses on addresses.id_rate = rates.rate_id where addresses.meter_id = new.meter_id limit 1;

    if (vlastDate is not null) then
		select max(quantity_kw) into vKwMeasure from measurements where meter_id and `date` = vlastDate;
        set new.price_measurement = (new.quantity_kw - vKwMeasure) * vValueRate;
	else
		set new.price_measurement = new.quantity_kw * vValueRate;
	end if;
End;
//

create trigger TAP_update_price_measurements after update on rates for each row
begin
	update measurements set price_measurement = (price_measurement / old.value) * new.value where measurement_id in
																					(select m.measurement_id	from measurements ms
																					inner join meters mt on mt.serialnumber = ms.meter_id
                                                                                    inner join addresses a on a.meter_id = ms.meter_id
                                                                                    inner join rates r on a.id_rate = r.rate_id
                                                                                    where r.rate_id = new.rate_id);
	call p_invoicing_update_rate(new.rate_id);
END;
//

#PROCEDURE

Create procedure p_invoicing_update_rate(in INrateId int)
Begin
	declare vNewTotal double;
    declare vinvoiceId int;
    declare vaddressId int;
    declare vmeterId int;
    declare vClientId int;
    declare vinitMeasur datetime;
    declare vfinalMeasur dateTime;
    declare vconsumption double;
    declare vinvoiceValue double;
	declare vFinished int default 0;
	declare newinvoiceValue double;

	declare cur_liquidate cursor for select iv.invoice_id,iv.address_id,iv.meter_id,iv.user_id,iv.initial_measurement,iv.finalmeasurement,iv.total_consumption,iv.invoice_value
					from invoice iv
                    inner join addresses a on a.address_id = iv.address_id
                    where a.id_rate = INrateId;

	DECLARE CONTINUE HANDLER FOR NOT FOUND SET vFinished = 1;
	OPEN cur_liquidate;
	fetch cur_liquidate into vinvoiceId,vaddressId,vmeterId,vClientId,vinitMeasur,vfinalMeasur,vconsumption,vinvoiceValue;

    while (vFinished = 0) DO

		set newinvoiceValue = (Select sum(price_measurement) from measurements where invoice_id = vinvoiceId) - vinvoiceValue;

        insert into invoices (address_id,meter_id,user_id,initial_measurement,final_measurements,total_consumption,invoice_value)
        values(vaddressId,vmeterId,vClientId,vinitMeasur,vfinalMeasur,vconsumption,vinvoiceValue);

        fetch cur_liquidate into vinvoiceId,vaddressId,vmeterId,vClientId,vinitMeasur,vfinalMeasur,vconsumption,vinvoiceValue;

    end while;

    close cur_liquidate;
end
//
Delimiter ;
call p_invoicing_update_rate;

/*4) Generar las estructuras necesarias para dar soporte a las consultas de mediciones
por fecha y por usuario , debido a que tenemos restricción de que estas no pueden demorar
más de dos segundos y tenemos previsto que tendremos 500.000.000 de mediciones en el
sistema en el mediano plazo. Este reporte incluirá :
● Cliente
● Medidor
● Fecha medición
● Medicion
● Consumo Kwh
● Consumo precio*/

#Creacion de View
CREATE VIEW measurements_report_by_date_user_view AS
SELECT  u.username as 'Usuario', u.name As 'Nombre',u.last_name as'Apellido',mt.serial_number as 'Medidor',ms.measurement_id as 'Medicion',ms.date as 'Fecha_medición',ms.quantity_kw as 'Consumo_Kwh',ms.price_measurement as 'Consumo_precio'
		from users u join addresses a ON a.user_id=u.user_id
        JOIN meters mt ON mt.serial_number= a.meter_id
        JOIN measurements ms ON mt.serial_number=ms.meter_id;

#Creacion de indices
CREATE INDEX measurement_info ON measurements(measurement_id,date) USING BTREE;
CREATE INDEX user_profile on users (username) Using btree;

#Creacion de procedimiento
DROP PROCEDURE IF EXISTS p_consult_User_measurements_byDates;
DELIMITER //
CREATE PROCEDURE p_consult_User_measurements_byDates(IN pUsername varchar(30), IN pSince DATETIME, IN pUntil DATETIME)
BEGIN
    DECLARE vFound INT DEFAULT 0;
    SELECT COUNT(*) INTO vFound FROM users WHERE username = pUsername;
    IF(vFound = 1) THEN
        IF(pSince > pUntil) THEN
            SIGNAL SQLSTATE '10001'
            SET MESSAGE_TEXT = 'The initial date must be lower than the objective date',
            MYSQL_ERRNO = 2.2;
        ELSE
            SELECT * FROM measurements_report_by_date_user_view
            WHERE Usuario = pUsername
            and Fecha_medición
            BETWEEN pSince AND pUntil;
        END IF;
    ELSE
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'User Does not exists',
        MYSQL_ERRNO = 2.3;
    END IF;
END//
Delimiter ;
#Testeo
SELECT * FROM measurements_report_by_date_user_view;
SELECT * FROM measurements_report_by_date_user_view WHERE Usuario = "Messias" and Fecha_medición BETWEEN "2021-06-19 02:47:01" AND "2021-06-23 11:30:01";
call p_consult_User_measurements_byDates("Brashan", "2021-06-19 02:47:01", "2021-06-26 11:30:01");
