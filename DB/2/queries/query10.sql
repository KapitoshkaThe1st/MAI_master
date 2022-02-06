-- Выбрать все уникальные названия городов, регионов и стран в одной колонке
select distinct CITY_NAME || ', ' || REGION || ', ' || COUNTRY as DATA from CITY;