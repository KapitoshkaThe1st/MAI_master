-- Функция для получения синонима тега.
-- Если у переданного тега есть синоним, вернется
-- синоним, иначе сам переденный тег.
create or replace function F_GET_TAG_MAIN_SYNONYM
( tag IN VARCHAR )
    return VARCHAR
    IS
    result VARCHAR(100) := null;
BEGIN

    begin
        select TARGETTAGNAME
        into result
        from SO_RU.TAGSYNONYMS
        where SOURCETAGNAME = tag;

    exception
        when no_data_found then
            result := tag;
    end;

    return result;
end;
