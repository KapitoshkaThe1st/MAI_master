-- Выбрать все заказы менеджеров с именем Henry
select * from SALES_ORDER
where MANAGER_ID in (
    select MANAGER_ID
    from MANAGER
    where MANAGER_FIRST_NAME = 'Henry');