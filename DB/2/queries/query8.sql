-- Выбрать все страны из таблицы CITY с количеством городов в них.
select COUNTRY, COUNT(*) as cities_count
from CITY
group by COUNTRY;