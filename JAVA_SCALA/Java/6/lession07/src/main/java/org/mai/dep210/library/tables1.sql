-- Создание таблиц
create table BOOKS
(
    BOOK_ID    INT not null,
    BOOK_TITLE VARCHAR(255) not null,
    STUDENT_ID INT default null
);

create unique index BOOKS_BOOK_ID_UINDEX
    on BOOKS (BOOK_ID);

alter table books
    add constraint BOOKS_PK
        primary key (BOOK_ID);

create table ABONENTS
(
    STUDENT_ID   INT not null,
    STUDENT_NAME VARCHAR(255) not null
);

create unique index ABONENTS_STUDENT_ID_UINDEX
    on ABONENTS (STUDENT_ID);

alter table ABONENTS
    add constraint ABONENTS_PK
        primary key (STUDENT_ID);

-- Заполнение данными
insert into ABONENTS (STUDENT_ID, STUDENT_NAME) VALUES ( 1, 'Petrov' );
insert into ABONENTS (STUDENT_ID, STUDENT_NAME) VALUES ( 2, 'Sidorov' );
insert into ABONENTS (STUDENT_ID, STUDENT_NAME) VALUES ( 3, 'Ivanov' );

commit;

insert into BOOKS (BOOK_ID, BOOK_TITLE, STUDENT_ID) VALUES ( 1, 'Treasure island', DEFAULT);
insert into BOOKS (BOOK_ID, BOOK_TITLE, STUDENT_ID) VALUES ( 2, 'Lord of the Ring', DEFAULT);
insert into BOOKS (BOOK_ID, BOOK_TITLE, STUDENT_ID) VALUES ( 3, 'Harry Potter', DEFAULT);
insert into BOOKS (BOOK_ID, BOOK_TITLE, STUDENT_ID) VALUES ( 4, 'Kolobok', DEFAULT);

commit;

-- select * from BOOKS;
-- select * from ABONENTS;

-- CREATE TABLE ABONENTS(
--     student_id int not null,
--     student_name varchar(255) not null,
--     CONSTRAINT PK_ABONENT PRIMARY KEY (student_id)
-- );
--
-- CREATE TABLE BOOKS(
--     book_id int not null,
--     book_title varchar(255) not null,
--     student_id int,
--     CONSTRAINT PK_BOOK PRIMARY KEY (book_id)
-- );
--
-- delete from ABONENTS;
--
-- insert into ABONENTS (student_id, student_name) values (1, 'petrov');
-- insert into ABONENTS (student_id, student_name) values (2, 'sidorov');
-- insert into ABONENTS (student_id, student_name) values (3, 'ivanov');
--
-- commit;
--
-- select * from ABONENTS;
--
-- delete from BOOKS;
--
-- insert into BOOKS (book_id, book_title, student_id) values ( 1, 'asdfgh', null);
-- insert into BOOKS (book_id, book_title, student_id) values ( 2, 'tsrhg', null);
-- insert into BOOKS (book_id, book_title, student_id) values ( 3, 'mjnbvc', null);
--
-- commit;
--
-- select * from BOOKS;