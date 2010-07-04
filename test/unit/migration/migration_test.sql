create table ADDRESSES(id int primary key auto_increment,street varchar(50),number integer,person_id integer,man_id integer,type_id integer)ENGINE = InnoDB;
create table CARS(id int primary key auto_increment,category integer,brand varchar(50),person_id integer,man_id integer,type_id integer)ENGINE = InnoDB;
create table PEOPLE(id int primary key auto_increment,birthday date,phone varchar(50),married boolean,wake time,createdAt timestamp,name varchar(50),age integer,job varchar(50))ENGINE = InnoDB;
create table personnel(id int primary key auto_increment,name varchar(50))ENGINE = InnoDB;
create table PETS(id int primary key auto_increment,name varchar(50),type varchar(50))ENGINE = InnoDB;
create table TYPES(car char(1),kreel float,kcar char(1),court smallint,kentier integer,reel float,kbigreal float,id int primary key auto_increment,entier integer,str varchar(50),koctet tinyint,kcourt smallint,longue bigint,bigreal float,klongue bigint,octet tinyint)ENGINE = InnoDB;
create table PEOPLE_PETS(parentId integer,childId integer)ENGINE = InnoDB;
