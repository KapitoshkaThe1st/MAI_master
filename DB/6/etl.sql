-- заполнение DIM_DATE --

-- поиск минимальной и максимальной даты создания вопроса, представленной в таблице
select min(CREATIONDATE) min_date, max(CREATIONDATE) max_date from so_ru.POSTS where POSTTYPEID = 1;

create sequence seq_dim_date_id;
-- drop table DIM_date;
-- delete from DIM_DATE;
-- drop sequence seq_dim_date_id;

-- Заполнение таблицы DIM_DATE. Заполняем с запасом по целым годам.
begin
    P_GENERATE_DIM_DATE(to_date('2010-01-01', 'YYYY-MM-DD'), to_date('2019-01-01', 'YYYY-MM-DD'));
end;

-- select * from dim_date order by date_id;

commit;
---------------------

-- Судя по всему, максимальное число тегов -- 5
-- (загуглил, да действительно, больше 5 не заложено в самом stackoverflow)
select max(c), min(c)
from (select count(*) c from so_ru.POSTTAGS group by POSTID);

-- И, судя по всему, не может быть вопросов без тегов
select * from so_ru.POSTS where tags = '' and POSTTYPEID = 1;

-- Заполнение DIM_TAG --

-- drop table stg_questions_split_tags;

-- Создаем временную таблицу, в которой все теги будут разбиты на отдельные поля.
-- Кроме того, некоторые теги заменены общепринятыми синонимамм.
CREATE TABLE stg_questions_split_tags as(
        select
            ID,
            CREATIONDATE,
            SCORE,
            VIEWCOUNT,
            F_GET_TAG_MAIN_SYNONYM(REGEXP_SUBSTR(TAGS, '([^<>]+)+', 1, 1)) tag_1,
            F_GET_TAG_MAIN_SYNONYM(REGEXP_SUBSTR(TAGS, '([^<>]+)+', 1, 2)) tag_2,
            F_GET_TAG_MAIN_SYNONYM(REGEXP_SUBSTR(TAGS, '([^<>]+)+', 1, 3)) tag_3,
            F_GET_TAG_MAIN_SYNONYM(REGEXP_SUBSTR(TAGS, '([^<>]+)+', 1, 4)) tag_4,
            F_GET_TAG_MAIN_SYNONYM(REGEXP_SUBSTR(TAGS, '([^<>]+)+', 1, 5)) tag_5
        from so_ru.POSTS where POSTTYPEID = 1
    );

-- select count(*) from stg_questions_split_tags;
-- select * from stg_questions_split_tags where tag_1 = tag_2;

-- Создаем временную таблицу, которая по сути своей одинакова с таблицей stg_questions_split_tags,
-- кроме того, что убраны дубликаты тегов, полученных в результати подстановки синонимов.
-- Это важно сделать, чтобы при аггрегации по тегам не учитывать два и более раз один и тот же вопрос.
CREATE TABLE stg_questions_unique_tags as (
    select id,
        CREATIONDATE,
        SCORE,
        VIEWCOUNT,
        TAG_1,
        case when TAG_2 = TAG_1 then null else tag_2 end                         tag_2,
        case when TAG_3 in (TAG_1, TAG_2) then null else tag_3 end               tag_3,
        case when TAG_4 in (TAG_1, TAG_2, TAG_3) then null else tag_4 end        tag_4,
        case when TAG_5 in (TAG_1, TAG_2, TAG_3, TAG_4) then null else tag_5 end tag_5
    from stg_questions_split_tags
);

-- select count(*) from stg_questions_unique_tags;
-- select * from stg_questions_unique_tags where tag_1 = tag_2;

-- Создаем таблицу всех тегов представленных среди вопросов
CREATE TABLE stg_all_tags as (
    select tag_1 tag from stg_questions_unique_tags
        union
    select tag_2 tag from stg_questions_unique_tags where tag_2 is not null
        union
    select tag_3 tag from stg_questions_unique_tags where tag_3 is not null
        union
    select tag_4 tag from stg_questions_unique_tags where tag_4 is not null
        union
    select tag_5 tag from stg_questions_unique_tags where tag_5 is not null
);

-- select * from stg_all_tags;

create sequence seq_dim_tag_id;
-- drop sequence tag_id_sequence;
-- delete from dim_tag;

-- Заполняем DIM_TAG
insert into dim_tag(tag_id, tag_name)
select
    seq_dim_tag_id.nextval as tag_id, tag from stg_all_tags;

commit;

---------------------

-- Создаем индексы, чтобы быстрее lookup'аться при заполнении таблицы фактов
create index I_DIM_DATE_NAME on DIM_DATE(DATE_NAME);
create index I_DIM_DATE_DATE on DIM_DATE("date");
create index I_DIM_TAG on DIM_TAG(TAG_NAME);

create sequence seq_fact_question_id;
-- drop sequence seq_fact_question_id;

-- Заполняем таблицу FACT_QUESTIONS
insert into FACT_QUESTIONS(question_id,
                           score,
                           view_count,
                           dim_date_id,
                           dim_tag_tag_1_id,
                           dim_tag_tag_2_id,
                           dim_tag_tag_3_id,
                           dim_tag_tag_4_id,
                           dim_tag_tag_5_id)
select seq_fact_question_id.nextval                                                         question_id,
       score,
       viewcount                                                                            view_count,
       (select DATE_ID from DIM_DATE where TRUNC(CREATIONDATE) = "date" and "level" = 1)    dim_date_id,
       (select tag_id from DIM_TAG where TAG_NAME = tag_1)                                  dim_tag_tag_1_id,
       (select tag_id from DIM_TAG where TAG_NAME = tag_2)                                  dim_tag_tag_2_id,
       (select tag_id from DIM_TAG where TAG_NAME = tag_3)                                  dim_tag_tag_3_id,
       (select tag_id from DIM_TAG where TAG_NAME = tag_4)                                  dim_tag_tag_4_id,
       (select tag_id from DIM_TAG where TAG_NAME = tag_5)                                  dim_tag_tag_5_id
from stg_questions_unique_tags;

commit;

-- select * from FACT_QUESTIONS order by QUESTION_ID;
-- select COUNT(*) from FACT_QUESTIONS;
--
truncate table FACT_QUESTIONS;
--
-- delete from FACT_QUESTIONS;
drop table FACT_QUESTIONS;
