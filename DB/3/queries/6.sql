-- Компания хочет премировать трех наиболее продуктивных (по объему продаж, конечно) менеджеров в каждой стране в 2014 году.
-- Выведите country, <список manager_last_name manager_first_name, разделенный запятыми> которым будет выплачена премия

with all_managers as (
    select MANAGER_ID from MANAGER
),
all_countries as (
    select country from CITY
),
sums as (
select
    V.COUNTRY,
    V.MANAGER_ID,
    sum(V.SALE_AMOUNT) sum
from V_FACT_SALE V
    right join all_managers AM on V.MANAGER_ID = AM.MANAGER_ID
    right join all_countries AC on V.COUNTRY = AC.COUNTRY
where
    trunc(SALE_DATE, 'YYYY') = TO_DATE('01.01.2014')
    and V.manager_id is not null
group by V.COUNTRY, V.MANAGER_ID
order by sum(SALE_AMOUNT)
),
ranking as (
        select sums.*, rank() over (partition by COUNTRY order by sums.sum desc) rank
    from sums
    )
-- select * from ranking;
select COUNTRY, listagg(concat(M.MANAGER_FIRST_NAME, concat(' ', M.MANAGER_LAST_NAME)) , ', ') within group ( order by rank) best_managers
from ranking R
         inner join MANAGER M on R.MANAGER_ID = M.MANAGER_ID
where rank between 1 and 3
group by COUNTRY;
