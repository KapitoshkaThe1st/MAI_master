create or replace procedure p_generate_dim_date
(
    date_from in date,
    date_to in date
) as
d_date date := date_from;
begin
    loop
        insert into DIM_DATE(date_id, date_name, "date", "level") values
            (seq_dim_date_id.nextval, to_char(d_date, 'DD.MM.YYYY'), d_date, 1);
        if (extract(day from d_date) = 1) then
            insert into DIM_DATE(date_id, date_name, "date", "level") values
            (seq_dim_date_id.nextval, to_char(d_date, 'MM.YYYY'), d_date, 2);
            if (extract(month from d_date) = 1) then
                insert into DIM_DATE(date_id, date_name, "date", "level") values
                    (seq_dim_date_id.nextval, to_char(d_date, 'YYYY'), d_date, 3);
            end if;
        end if;
        d_date := d_date + 1;
        exit when d_date >= date_to;
    end loop;
end p_generate_dim_date;

-- drop procedure p_generate_dim_date;