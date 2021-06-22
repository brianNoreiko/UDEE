CREATE DATABASE IF NOT EXISTS UDEE;

USE UDEE;

CREATE TABLE IF NOT EXISTS brands(
                                     brandId INT NOT NULL AUTO_INCREMENT,
                                     `name` VARCHAR(50) NOT NULL,
                                     CONSTRAINT pk_brand PRIMARY KEY (id_brand)
);

CREATE TABLE IF NOT EXISTS models(
                                     modelId INT NOT NULL AUTO_INCREMENT,
                                     brandId INT NOT NULL,
                                     `name` VARCHAR(50) NOT NULL,
                                     CONSTRAINT pk_model PRIMARY KEY (modelId),
                                     CONSTRAINT fk_model_brand FOREIGN KEY (brandId) REFERENCES brands(brandId)
);

CREATE TABLE IF NOT EXISTS meters (
                                      serial_number int NOT NULL AUTO_INCREMENT,
                                      modelId INT NOT NULL,
                                      `password` VARCHAR(30) NOT NULL,
                                      CONSTRAINT pk_meter PRIMARY KEY (serial_number),
                                      CONSTRAINT fk_meter_model FOREIGN KEY (modelId) REFERENCES models(modelId)
);

CREATE TABLE IF NOT EXISTS users(
                                    userId INT NOT NULL AUTO_INCREMENT,
                                    `name` VARCHAR(50) NOT NULL,
                                    last_name VARCHAR(50) NOT NULL,
                                    username VARCHAR(50) NOT NULL,
                                    email VARCHAR(50) NOT NULL,
                                    `password` VARCHAR(30) NOT NULL,
                                    type_user INT NOT NULL DEFAULT 1,
                                    CONSTRAINT pk_user PRIMARY KEY (userId),
                                    CONSTRAINT unq_username UNIQUE (username),
									CONSTRAINT check_type_user check (type_user BETWEEN 0 AND 1)
);

/*
ALTER TABLE users CHANGE COLUMN type_user type_user INT NOT NULL DEFAULT 1;
ALTER TABLE users add constraint check_type_user check (type_user between 0 and 1);
*/

CREATE TABLE IF NOT EXISTS rates(
                                    rateId INT NOT NULL AUTO_INCREMENT,
                                    `value` DOUBLE NOT NULL,
                                    type_rate VARCHAR(50) NOT NULL,
                                    CONSTRAINT pk_rate PRIMARY KEY (rateId)
);

CREATE TABLE IF NOT EXISTS addresses(
                                        addressId INT NOT NULL AUTO_INCREMENT,
                                        meterId INT NOT NULL,
                                        userId INT NOT NULL,
                                        rateId INT NULL,
                                        street VARCHAR(40) NOT NULL,
                                        number INT NOT NULL,
                                        apartment VARCHAR(10),
                                        CONSTRAINT pk_address PRIMARY KEY (addressId),
                                        CONSTRAINT fk_address_meter FOREIGN KEY (meterId) REFERENCES meters(meterId),
                                        CONSTRAINT fk_address_user FOREIGN KEY (userId) REFERENCES users(userId),
                                        CONSTRAINT fk_address_rate FOREIGN KEY (rateId) REFERENCES rates(rateId)
);


CREATE TABLE IF NOT EXISTS invoices(
	invoiceId INT NOT NULL AUTO_INCREMENT,
	addressId INT NOT NULL,
	meterId INT NOT NULL,
	userId INT NOT NULL,
	initial_measurement DATETIME NOT NULL,
	final_measurement DATETIME NOT NULL,
	total_consumption DOUBLE NOT NULL,
	total_payable DOUBLE,
	expiration DATETIME DEFAULT NOW(),
	`date` DATETIME DEFAULT NOW(),
	payed BOOL DEFAULT FALSE,
	CONSTRAINT pk_bill PRIMARY KEY (invoiceId),
	CONSTRAINT fk_bill_address FOREIGN KEY (addressId) REFERENCES addresses(addressId),
	CONSTRAINT fk_bil_meter FOREIGN KEY (meterId) REFERENCES meters(meterId),
	CONSTRAINT fk_bill_user FOREIGN KEY (userId) REFERENCES users(userId)
);

CREATE TABLE IF NOT EXISTS measurements(
	measurementId INT NOT NULL AUTO_INCREMENT,
	meterId  INT NOT NULL,
	billId  INT,
	`date` DATETIME,
	quantity_kw DOUBLE NOT NULL,
	price_measurement DOUBLE DEFAULT 0,
	CONSTRAINT pk_measurement PRIMARY KEY (measurementId),
	CONSTRAINT fk_measurement_meter FOREIGN KEY (meterId) REFERENCES meters(meterId),
	CONSTRAINT fk_measurement_bill FOREIGN KEY (billId) REFERENCES bills(billId)
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
CREATE USER 'backoffice' IDENTIFIED BY 'backofficeRKT';

GRANT SELECT,INSERT,UPDATE,DELETE ON udee.rates TO 'backoffice';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.meters TO 'backoffice';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.users TO 'backoffice';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.addresses TO 'backoffice';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.models TO 'backoffice';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.brands TO 'backoffice';
GRANT SELECT,INSERT,UPDATE,DELETE ON udee.rates TO 'backoffice';

#/------------------------------CLIENTS------------------------------/
CREATE USER 'clients' IDENTIFIED BY 'clientsRKT'; 

GRANT SELECT ON udee.measurements TO 'clients';
GRANT SELECT ON udee.bills TO 'clients';

#/------------------------------METERS------------------------------/
CREATE USER 'meters' IDENTIFIED BY 'metersRKT'; 

GRANT INSERT ON udee.measurements TO 'meters';

#/------------------------------BILLING------------------------------/
CREATE USER 'billing' IDENTIFIED BY 'billingRKT'; 

GRANT EXECUTE ON PROCEDURE p_all_clients_billing TO billing;
GRANT EXECUTE ON PROCEDURE p_billing_update_rate TO billing;


