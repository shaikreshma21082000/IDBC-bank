create database IDBC;
use idbc;

create table customer (
cid integer primary key auto_increment,
Fname varchar(20),
age integer,
phnno long,
city varchar(20));
alter table customer auto_increment=1001;
insert into customer(fname,age,phnno,city) values('Reshma',22,7386768425,'Manuguru');
insert into customer(fname,age,phnno,city) values('Puja',23,7036865483,'Khammam');
insert into customer(fname,age,phnno,city) values('Anu',24,7386476609,'Hyderabad');
insert into customer(fname,age,phnno,city) values('akhila',20,9866595850,'suryapeta');
insert into customer(fname,age,phnno,city) values('Lavanya',21,9959585056,'wyra');




create table accounts(
accid varchar(12) primary key,
cid integer not null,foreign key (cid) references customer(cid),
accpin integer not null,
acctype varchar(10) not null,
balance integer,
AccountCreatedDate datetime default current_timestamp,
LastUpdatedIntrestDate datetime);
insert into accounts(accid,cid,accpin,acctype,balance,AccountCreatedDate)  values('IDBC72256526',1001,1234,'savings',50000,'2017-02-12');
insert into accounts(accid,cid,accpin,acctype,balance,AccountCreatedDate)  values('IDBC72256527',1002,4567,'savings',50000,'2018-12-21');
insert into accounts(accid,cid,accpin,acctype,balance,AccountCreatedDate)  values('IDBC72256528',1003,7890,'pay',70000,'2015-06-02');
insert into accounts(accid,cid,accpin,acctype,balance,AccountCreatedDate)  values('IDBC72256529',1004,2468,'pay',10000,'2000-01-24');
insert into accounts(accid,cid,accpin,acctype,balance,AccountCreatedDate)  values('IDBC72256530',1005,3579,'pay',90000,'2019-12-09');


create table transactions (
tid integer primary key auto_increment,
accid varchar(12),foreign key (accid) references accounts(accid),
transtype varchar(25) not null,
SenderOrReciverAccNo varchar(12),
transdate datetime default current_timestamp,
balance integer,
breifing varchar(50));
alter table transactions auto_increment=1001;


select * from accounts;
select * from customer;
select * from transactions;


drop database idbc;

SELECT DATEDIFF('2017/08/25', '2011/08/25') AS DateDiff;
#current_date()--will returns todayis date
select curdate();
update accounts
set balance=4000,LastUpdatedIntrestDate=current_date() 
where accid='IDBC72256526';

SELECT DATE_SUB("2017-06-15", INTERVAL 10 DAY);
SELECT date_sub("2017-06-15", INTERVAL 10 DAY) as date;
create table dates(
today datetime default localtimestamp);
insert into dates values();
select * from dates;
select curdate();

update accounts
set LastUpdatedIntrestDate=null
where accid in ('IDBC72256526','IDBC72256527');