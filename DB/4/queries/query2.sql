-- 2. Для каждой директории посчитать объем занимаемого места на диске (с учетом всех вложенных папок)
-- ID, Название, Путь до корня, total_size

with C(ID, FILE_SIZE, ROOT_ID, TYPE) as(
    select
        f.ID,
        f.FILE_SIZE,
        f.ID ROOT_ID,
        f.TYPE
    from FILE_SYSTEM f
    union all
    select
        f.ID,
        f.FILE_SIZE,
        c.ROOT_ID,
        f.TYPE
    from
        FILE_SYSTEM f
            inner join C on (f.PARENT_ID = c.ID)
),
     paths as(
         select ID,
                NAME,
                SYS_CONNECT_BY_PATH(name, '/') PATH
         from FILE_SYSTEM
         connect by prior id = PARENT_ID
         start with PARENT_ID is null
     )
select
    f.ID,
    f.NAME,
    P.PATH,
    s.TOTAL_SIZE
from
    FILE_SYSTEM f
        inner join (
        select
            ROOT_ID,
            sum(FILE_SIZE) TOTAL_SIZE
        from C
        group by ROOT_ID
    ) s on (f.id = s.ROOT_ID)
        inner join paths p on(f.ID = p.ID)
where f.TYPE = 'DIR'
order by TOTAL_SIZE desc, PATH;