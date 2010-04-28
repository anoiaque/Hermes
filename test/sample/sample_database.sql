create database HERMES;
use HERMES;
create table  PEOPLE(
id int primary key auto_increment,
age int, 
name varchar(250),
phone varchar(250),
married boolean,
job varchar(250)
) ENGINE = InnoDB;
create table ADDRESSES(
id int primary key auto_increment,
number int,
street varchar(250),
person_id int
)ENGINE = InnoDB;
create table PETS(
id int primary key auto_increment,
type varchar(20),
name varchar(20)
)ENGINE = InnoDB;
create table  PERSONNEL(
id int primary key auto_increment,
name varchar(250)
)ENGINE = InnoDB;
create table CARS(
id int primary key auto_increment,
brand varchar(50),
category int,
person_id int
)ENGINE = InnoDB;