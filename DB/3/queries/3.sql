-- Для планирования закупок, компанию оценивает динамику роста продаж по товарам.
-- Динамика оценивается как отношение объема продаж в текущем месяце к предыдущему.
-- Выведите товары, которые демонстрировали наиболее высокие темпы роста продаж в течение первого полугодия 2014 года.

with ALL_MONTHS as (
    select ADD_MONTHS(TO_DATE('01.12.2013', 'DD.MM.YYYY'), level-1) SALE_MONTH
    from dual connect by level <= 7
),
ALL_PRODUCTS as (
--     select PRODUCT_ID from PRODUCT
--     select distinct PRODUCT_ID from V_FACT_SALE
    select distinct PRODUCT_ID
    from V_FACT_SALE
    where trunc(SALE_DATE, 'MM') between TO_DATE('01.12.2013', 'DD.MM.YYYY') and TO_DATE('01.06.2014', 'DD.MM.YYYY')
    order by PRODUCT_ID
),
ALL_PRODUCTS_MONTH as (
    select * from ALL_PRODUCTS cross join ALL_MONTHS
),
ALL_SALES as (
--     select M.PRODUCT_ID, M.SALE_MONTH, COALESCE (V.SALE_AMOUNT, 0) SALE_AMOUNT
    select M.PRODUCT_ID,
           M.SALE_MONTH,
           V.SALE_AMOUNT SALE_AMOUNT
    from V_FACT_SALE V
        right join ALL_PRODUCTS_MONTH M on
            V.PRODUCT_ID = M.PRODUCT_ID
            and trunc(V.SALE_DATE, 'MM') = M.SALE_MONTH
    ),
SUM_SALES as (
    select PRODUCT_ID, SALE_MONTH, SUM(SALE_AMOUNT) S
    from ALL_SALES
    group by PRODUCT_ID, SALE_MONTH
    order by PRODUCT_ID, SALE_MONTH
    ),
ALL_DYNAMICS as(
    select PRODUCT_ID,
        SALE_MONTH,
--         COALESCE(S, 0) S,
--         LAG(S, 1) over ( partition by PRODUCT_ID order by SALE_MONTH) LAG_S,
        COALESCE(COALESCE(S, 0) / LAG(S, 1) over ( partition by PRODUCT_ID order by SALE_MONTH), 0) DYNAMIC
    from SUM_SALES
    )

select num, P.PRODUCT_NAME, DYNAMYC from (select PRODUCT_ID,
                      SUM(DYNAMIC) DYNAMYC,
                      rank() over ( order by sum(DYNAMIC) desc ) num
               from ALL_DYNAMICS
               group by PRODUCT_ID
              ) T
inner join PRODUCT P on T.PRODUCT_ID = P.PRODUCT_ID
where num <= 10
order by num;