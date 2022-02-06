-- Менеджер получает оклад в 30 000 + 5% от суммы своих продаж в месяц. Средняя наценка стоимости товара - 10%
-- Посчитайте прибыль предприятия за 2014 год по месяцам (сумма продаж - (исходная стоимость товаров + зарплата))
-- month, sales_amount, salary_amount, profit_amount

-- считаем всех трудоустроенных менеджеров активными, т.е. тех, кто совершил хотя бы одну продажу

with stats as (
    select
        trunc(SALE_DATE, 'MM') sale_month,
        MANAGER_ID,
        SALE_AMOUNT,
        sum( SALE_AMOUNT ) over ( partition by trunc(SALE_DATE, 'MM') ) sales_amount,
        sum( SALE_AMOUNT ) over ( partition by trunc(SALE_DATE, 'MM') ) / 1.1 buys_amount,
--     count( distinct MANAGER_ID ) over ( partition by trunc(SALE_DATE, 'MM') ) n_managers,
--     30000 * count( distinct MANAGER_ID ) over ( partition by trunc(SALE_DATE, 'MM') ) base_salary_amount,
        30000 * count( distinct MANAGER_ID ) over ( partition by trunc(SALE_DATE, 'MM') ) + 0.05 * sum( SALE_AMOUNT ) over ( partition by trunc(SALE_DATE, 'MM') ) salary_amount,
        sum( SALE_AMOUNT ) over ( partition by trunc(SALE_DATE, 'MM') ) * (1 - 0.05 - 1/1.1) - 30000 * count( distinct MANAGER_ID ) over ( partition by trunc(SALE_DATE, 'MM') ) profit_amount
    from V_FACT_SALE
    where trunc(SALE_DATE, 'YYYY') = TO_DATE('01.01.2014')
    order by trunc(SALE_DATE, 'MM')
) select distinct sale_month, sales_amount, salary_amount, profit_amount from stats;

-- работаем в жуткий убыток ???