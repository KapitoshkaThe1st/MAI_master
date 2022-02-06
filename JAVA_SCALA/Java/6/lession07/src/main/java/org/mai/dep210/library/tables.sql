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