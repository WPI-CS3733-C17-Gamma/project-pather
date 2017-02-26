make table GraphNode (ID integer primary key, X integer, Y integer, floor varchar(20));
make table Edge (ID1 integer references ID, ID2 integer references )
make table Room(ID integer, Name varchar(30), nID integer)
make table RoomEntryAssoc(eID integer, rID integer)


insert into Graphnode values (3, 20, 30, 'second');
insert into Edge values (2, 3);
insert into Room values (3, 'ER', 5);
insert into RoomEntryAssoc values (1, 6);




select * from GraphNode where floor='second';
select * from Edge where ID2=2;
select * from Room where Name='ER';
select * from RoomEntryAssoc where eID=1;

