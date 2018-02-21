create table "USER"."test01" (ID int, NAME VARCHAR(20));

drop table device01;

rename table "USER"."192.168.0.12_STATISTICS01" to STAT01_192168000012;

insert into "USER"."ACCOUNT01" values (default, 'admin', '1234567890', 'admin', 'Administrator', 'stevenyang@via.com.tw', null, null);

truncate table device02;

select * from ( select row_number() over() as R, APP.PKT_TYPE_STAT.* from APP.PKT_TYPE_STAT ) as TR where R > 3000 AND R <= 4000;
select * from APP.PKT_TYPE_STAT_TEMP order by time desc;
select sum(oct_rx_tot) from "USER"."192.168.0.101_STATISTICS01" where rec_time > '2014-03-03 17:15:00' and rec_time < '2014-03-03 17:17:00';
select * from "USER"."192.168.0.101_STATISTICS01" where port_id = 14 and rec_time < '2014-03-03 17:35:00.000';
select count(*) from "USER"."192.168.0.100_STATISTICS01";
select * from "USER"."192.168.0.101_STATISTICS01" order by id desc fetch first row only;
select * from "USER"."DEVICE01";

DELETE FROM "USER"."DEVICE01" WHERE PHY_ADDR = '00:12:7b:72:00:a1';