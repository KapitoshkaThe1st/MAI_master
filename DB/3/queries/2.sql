-- 2. Компания хочет оптимизировать количество офисов, проанализировав относительные объемы продаж по офисам в течение периода с 2013-2014 гг.
-- Выведите год, office_id, city_name, country, относительный объем продаж за текущий год
-- Офисы, которые демонстрируют наименьший относительной объем в течение двух лет скорее всего будут закрыты.

-- select SALE_DATE from V_FACT_SALE where MANAGER_ID = 12

-- -- есть менеджер без айди офиса
select * from MANAGER where OFFICE_ID is null;
--
-- -- судя по тому, что минимальная дата продажи за 2006 год, будем считать что все офисы открылись к 2013-14 годам,
-- -- и если нет продаж, то это не потому что их не существовало, а потому сто кто-то плохо работает
select OFFICE_ID, min(SALE_DATE) from V_FACT_SALE group by OFFICE_ID order by min(SALE_DATE) desc;

with ALL_YEARS as (
    select ADD_MONTHS(TO_DATE('01.01.2013', 'DD.MM.YYYY'), (level-1) * 12) SALE_YEAR
    from dual connect by level <= 2
),
     ALL_MANAGERS_WITH_OFFICES as (
         select MANAGER_ID, OFFICE_ID
         from MANAGER
         where not OFFICE_ID is null
     ),
     MANAGERS_YEARS as (
         select *
         from ALL_YEARS
                  cross join ALL_MANAGERS_WITH_OFFICES
     ),
all_sales as (
    select
    SALE_YEAR,
    MY.OFFICE_ID,
    COALESCE(SALE_AMOUNT, 0)
        sale_amount
    from V_FACT_SALE V
        right join MANAGERS_YEARS MY on (trunc(SALE_DATE, 'YYYY') = SALE_YEAR and V.MANAGER_ID = MY.MANAGER_ID)
    where
          SALE_YEAR between TO_DATE('01.01.2013', 'DD.MM.YYYY') and TO_DATE('31.12.2014', 'DD.MM.YYYY')
    ),
stats as(
    select distinct
    SALE_YEAR,
    OFFICE_ID,
--     SALE_AMOUNT,
--     sum(SALE_AMOUNT) over (partition by SALE_YEAR, OFFICE_ID) sales_amount,
--     sum(SALE_AMOUNT) over ( partition by SALE_YEAR) year_sales,
    sum(SALE_AMOUNT) over (partition by SALE_YEAR, OFFICE_ID) / sum(SALE_AMOUNT) over ( partition by SALE_YEAR) part
    from all_sales
    )
select SALE_YEAR,
       S.OFFICE_ID,
       C.CITY_NAME,
       C.COUNTRY,
       S.part
from stats S
    inner join OFFICE O on (S.OFFICE_ID = O.OFFICE_ID)
    inner join CITY C on O.CITY_ID = C.CITY_ID
order by SALE_YEAR, part, OFFICE_ID;