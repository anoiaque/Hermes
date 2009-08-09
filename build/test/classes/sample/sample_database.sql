create database HERMES;
use HERMES;
create table  PERSON(
id int primary key auto_increment,
age int, 
nom varchar(250),
adresse_id int
);
create table ADRESS(
id int primary key auto_increment,
numero int,
rue varchar(250)
);
create table PET(
id int primary key auto_increment,
type varchar(20),
name varchar(20)
);
create table  PERSONNEL(
id int primary key auto_increment,
nom varchar(250)
);