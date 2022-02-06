-- create table COMMENTS(
--      id Int,
--      displayName varchar(200),
--      location varchar(200),
--      about varchar(1000),
--      reputation Int,
--      views Int,
--      upVotes Int,
--      downVotes Int,
--      accountId Int,
--      creationDate Timestamp,
--      lastAccessDate Timestamp
-- );

create table Posts(
    id Int,
    title varchar(200),
    body varchar(200),
    score Int,
    viewCount Int,
    answerCount Int,
    commentCount Int,
    ownerUserId Int,
    lastEditorUserId Int,
    acceptedAnswerId Int,
    creationDate timestamp,
    lastEditDate timestamp,
    lastActivityDate timestamp,
    tags varchar(300)
);

create table Posts(
    id Int,
    postId Int,
    score Int,
    text varchar(1000),
    creationDate timestamp,
    userId Int
);

create table Test(
    id Int,
    name VARCHAR(200)
);

insert into Test(id, name) values (1, 'tdhy');
insert into Test(id, name) values (2, 'rstn');
insert into Test(id, name) values (3, 'mhfgds');

commit;

select * from Test;

drop table Test;
truncate table Test;
create table if not exists Test(
    id Int,
    name VARCHAR(200)
);

select
    id,
    display_name,
    location,
    reputation,
    views,
    up_votes,
    down_votes,
    account_id,
    creation_date,
    last_access_date
from users;

select id, display_name, location, reputation, views, up_votes, down_votes, account_id, creation_date, last_access_date from users where id between 4000 and 4100;
select id, display_name, location from users where location != '';

-- extract --query "select id, display_name, location from users where location != '';"