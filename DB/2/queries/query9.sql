-- Выбрать количество товаров (QTY), проданное с 1 по 30 января 2016 года.
select SUM(PRODUCT_QTY) as sold_products_count
from SALES_ORDER_LINE, SALES_ORDER
where SALES_ORDER_LINE.SALES_ORDER_ID = SALES_ORDER.SALES_ORDER_ID and
    ORDER_DATE between TO_DATE('01.01.2016', 'DD.MM.YYYY')
    and TO_DATE('30.01.2016', 'DD.MM.YYYY');

-- или так
select SUM(PRODUCT_QTY) as sold_products_count
from SALES_ORDER_LINE
where SALES_ORDER_ID in(
    select SALES_ORDER_ID
    from SALES_ORDER
    where ORDER_DATE between TO_DATE('01.01.2016', 'DD.MM.YYYY')
            and TO_DATE('30.01.2016', 'DD.MM.YYYY')
    );