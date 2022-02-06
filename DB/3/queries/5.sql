-- Найдите вклад в общую прибыль за 2014 год 10% наиболее дорогих товаров и 10% наиболее дешевых товаров.
-- Выведите product_id, product_name, total_sale_amount, percent

with STATS as (
    select distinct PRODUCT_ID,
           PRODUCT_NAME,
           avg(SALE_PRICE) over ( partition by PRODUCT_ID)                            avg,
           sum(SALE_AMOUNT) over ( partition by PRODUCT_ID)                            total_sale_amount,
           sum(SALE_AMOUNT) over ( partition by PRODUCT_ID) / sum(SALE_AMOUNT) over ( ) * 100 percent
    from V_FACT_SALE
    where trunc(SALE_DATE, 'YYYY') = TO_DATE('01.01.2014')
--     order by avg(SALE_PRICE) over ( partition by PRODUCT_ID)
),
NTILE_STATS as (
    select PRODUCT_ID,
           PRODUCT_NAME,
           total_sale_amount,
           percent,
           NTILE(10) over ( order by avg ) n
    from STATS
)
select
       (case when n=10 then 'EXPENSIVE'
           else 'CHEAP' end) price_category,
       PRODUCT_ID,
       PRODUCT_NAME,
       total_sale_amount,
       percent
from NTILE_STATS
where N in (1, 10);