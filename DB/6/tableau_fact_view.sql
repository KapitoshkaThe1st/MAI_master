create or replace view V_AGG_QUESTIONS_BY_DAY AS
select AGG_QUESTIONS.*
from
    AGG_QUESTIONS
    join DIM_DATE DD on AGG_QUESTIONS.DATE_ID = DD.DATE_ID
where "level" = 1 and AGG_QUESTIONS.DATE_ID is not null and TAG_ID is not null;