-- 3. Добавить в запрос: сколько процентов директория занимает  места относительно всех среди своих соседей (siblings)
-- ID, Название, Путь до корня, total_size, ratio

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
     ),
     stats as(
         select
             ROOT_ID,
             sum(FILE_SIZE) TOTAL_SIZE
         from C
         group by ROOT_ID
     )
select
    f.ID,
    f.NAME,
    P.PATH,
    ss.TOTAL_SIZE,
    case when s.TOTAL_SIZE = 0 then 1 else ss.TOTAL_SIZE / s.TOTAL_SIZE end RATIO
from FILE_SYSTEM f
         inner join stats s on(COALESCE(f.PARENT_ID, f.ID) = s.ROOT_ID)
         inner join stats ss on(f.ID = ss.ROOT_ID)
         inner join paths P on(f.ID = p.ID)
where f.TYPE = 'DIR'
order by ss.TOTAL_SIZE desc, path;