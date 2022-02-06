-- Выведите самый дешевый и самый дорогой товар, проданный за каждый месяц в течение 2014 года.
-- cheapest_product_id, cheapest_product_name, expensive_product_id, expensive_product_name, month, cheapest_price, expensive_price

select PRODUCT_ID, PRODUCT_ID=PRODUCT_ID from PRODUCT;

with stats as (
    select trunc(SALE_DATE, 'MM') month,
           PRODUCT_ID,
           PRODUCT_NAME,
           SALE_PRICE,
           min(SALE_PRICE) over ( partition by trunc(SALE_DATE, 'MM')) min_price,
           max(SALE_PRICE) over ( partition by trunc(SALE_DATE, 'MM')) max_price
    from V_FACT_SALE
    where trunc(SALE_DATE, 'YYYY') = TO_DATE('01.01.2014')
),
minmax as (
    select stats.*,
           (case when min_price = SALE_PRICE then 1 else 0 end) min_flag
    from stats
    where min_price = SALE_PRICE
       or max_price = SALE_PRICE
)
select m1.PRODUCT_ID cheapest_product_id,
       m1.PRODUCT_NAME cheapest_product_name,
       m2.PRODUCT_ID expensive_product_id,
       m2.PRODUCT_NAME expensive_product_name,
       m1.month,
       m1.min_price cheapest_price,
       m1.max_price expensive_price
from minmax M1
    inner join minmax M2
        on (M1.month = m2.month
                and m1.min_flag > m2.min_flag);
