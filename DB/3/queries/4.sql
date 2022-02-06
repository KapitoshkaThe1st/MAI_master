-- Напишите запрос, который выводит отчет о прибыли компании за 2014 год: помесячно и поквартально.
-- Отчет включает сумму прибыли за период и накопительную сумму прибыли с начала года по текущий период.

with ALL_MONTHS as (
    select ADD_MONTHS(TO_DATE('01.01.2014', 'DD.MM.YYYY'), level-1) SALE_MONTH
    from dual connect by level <= 12
),
MONTH_SUMS as (
    select trunc(SALE_DATE, 'MM') SALE_MONTH,
        COALESCE(SUM(SALE_AMOUNT), 0) MONTH_SUM
    from V_FACT_SALE
        right join ALL_MONTHS on trunc(SALE_DATE, 'MM') = SALE_MONTH
    group by trunc(SALE_DATE, 'MM')
)
select SALE_MONTH,
       MONTH_SUM,
       SUM(MONTH_SUM) over ( order by SALE_MONTH) CUM_MONTH_SUM,
           SUM(MONTH_SUM) over ( partition by trunc(SALE_MONTH, 'Q') ) QUARTER_SUM,
       SUM(MONTH_SUM) over ( order by trunc(SALE_MONTH, 'Q') range between unbounded preceding and current row ) CUM_QUARTER_SUM
from MONTH_SUMS
order by SALE_MONTH;
