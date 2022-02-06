-- 1. Каждый месяц компания выдает премию в размере 5% от суммы продаж менеджеру, который за предыдущие 3 месяца продал товаров на самую большую сумму
-- Выведите месяц, manager_id, manager_first_name, manager_last_name, премию за период с января по декабрь 2014 года

with ALL_MONTHS as (
    select ADD_MONTHS(TO_DATE('01.10.2013', 'DD.MM.YYYY'), level-1) SALE_MONTH
    from dual connect by level <= 15
),
     ALL_MANAGERS as (
         select MANAGER_ID, MANAGER_FIRST_NAME, MANAGER_LAST_NAME from MANAGER
     ),
     ALL_MANAGERS_MONTHS as (
         select SALE_MONTH, MANAGER_ID, MANAGER_FIRST_NAME, MANAGER_LAST_NAME
         from ALL_MONTHS cross join ALL_MANAGERS
     ),
     step1 as
         (
             select
                 SALES_ORDER_ID,
                 SALE_MONTH,
                 M.manager_id,
                 M.MANAGER_FIRST_NAME,
                 M.MANAGER_LAST_NAME,
                 SALE_AMOUNT
             from V_FACT_SALE F
                right outer join ALL_MANAGERS_MONTHS M on (TRUNC(SALE_DATE, 'MONTH')=M.SALE_MONTH and F.MANAGER_ID=M.MANAGER_ID)
         ),
--         select * from step1 where manager_id=362;
     step2 as (select SALES_ORDER_ID,
                      SALE_MONTH,
                      manager_id,
                      MANAGER_FIRST_NAME,
                      MANAGER_LAST_NAME,
                      SALE_AMOUNT,
                      sum(SALE_AMOUNT) over (partition by MANAGER_ID order by SALE_MONTH RANGE BETWEEN INTERVAL '3' MONTH PRECEDING AND INTERVAL '1' MONTH PRECEDING) PREV_SALE_AMOUNT
               from step1 F
               where SALE_MONTH between
                         TO_DATE('01.10.2013', 'DD.MM.YYYY') and
                         TO_DATE('31.12.2014', 'DD.MM.YYYY')
--   and MANAGER_ID = 362
     ),
     step3 as (
         select SALE_MONTH,
                manager_id,
                MANAGER_FIRST_NAME,
                MANAGER_LAST_NAME,
                MAX(PREV_SALE_AMOUNT)*0.05 BONUS
         from STEP2
         where SALE_MONTH >= TRUNC(TO_DATE('01.01.2014'), 'MM') -- добавил к готовому вопросу, который скидывалии, чтобы вывести только за 2014 год
         group by
             SALE_MONTH,
             manager_id,
             MANAGER_FIRST_NAME,
             MANAGER_LAST_NAME
     ),
     step4 as (
         select
             SALE_MONTH,
             manager_id,
             MANAGER_FIRST_NAME,
             MANAGER_LAST_NAME,
             BONUS,
             MAX(BONUS) OVER (PARTITION BY SALE_MONTH) MAX_BONUS
         from STEP3
     )
select * from STEP4 where MAX_BONUS = BONUS;
