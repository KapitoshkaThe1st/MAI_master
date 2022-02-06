-- Вывести имена и фамилии менеджер(ов), продавшего товаров в январе 2016 года
-- на наибольшую сумму
WITH sums AS
         (
             select MANAGER_ID, SUM(PRODUCT_QTY * PRODUCT_PRICE) as sum
             from SALES_ORDER_LINE
                      inner join SALES_ORDER SO on SALES_ORDER_LINE.SALES_ORDER_ID = SO.SALES_ORDER_ID
             where ORDER_DATE between TO_DATE('01.01.2016', 'DD.MM.YYYY') and TO_DATE('31.01.2016', 'DD.MM.YYYY')
             group by MANAGER_ID
)
select MANAGER_FIRST_NAME || ' ' || MANAGER_LAST_NAME as NAME
from sums
        inner join MANAGER M on sums.MANAGER_ID = M.MANAGER_ID
where sum = (select MAX(sum) from sums);