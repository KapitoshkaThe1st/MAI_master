-- Выбрать все заказы, введенные после 1 января 2016 года и до 15 июля 2016 года
select *
from SALES_ORDER
where ORDER_DATE > TO_DATE('01.01.2016', 'DD.MM.YYYY')
    and ORDER_DATE < TO_DATE('15.07.2016', 'DD.MM.YYYY');