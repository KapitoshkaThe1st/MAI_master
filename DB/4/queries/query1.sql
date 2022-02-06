-- 1. Вывести все директории в виде:
-- ID, Название, Путь до корня

with ALL_FILES as (
    select ID,
           PARENT_ID,
           NAME,
           SYS_CONNECT_BY_PATH(name, '/') PATH,
           TYPE
    from FILE_SYSTEM
    connect by nocycle prior id = PARENT_ID
    start with PARENT_ID is null
    order siblings by name
)
select ID, NAME, PATH from ALL_FILES where TYPE = 'DIR';