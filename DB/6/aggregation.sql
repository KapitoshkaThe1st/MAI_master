drop table STG_DATE_TAG_AGGS;

-- создаем таблицу в которую аггрегируются все факты по дате и каждому из тегов
CREATE TABLE STG_DATE_TAG_AGGS AS
WITH STATS_FOR_ALL_TAGS AS (
    -- аггрегация по 1-му тегу
    SELECT
           DIM_DATE_ID      DATE_ID,
           DIM_TAG_TAG_1_ID TAG_ID,
           AVG(SCORE)       AVG_SCORE,
           MIN(SCORE)       MIN_SCORE,
           MAX(SCORE)       MAX_SCORE,
           AVG(VIEW_COUNT)  AVG_VIEW_COUNT,
           MAX(VIEW_COUNT)  MAX_VIEW_COUNT,
           MIN(VIEW_COUNT)  MIN_VIEW_COUNT,
           SUM(VIEW_COUNT)  TOTAL_VIEW_COUNT,
           COUNT(*)         QUESTIONS_COUNT
    FROM FACT_QUESTIONS
    GROUP BY
        DIM_DATE_ID,
        DIM_TAG_TAG_1_ID
    UNION ALL
    -- аггрегация по 2-му тегу
    SELECT
           DIM_DATE_ID      DATE_ID,
           DIM_TAG_TAG_2_ID TAG_ID,
           AVG(SCORE)       AVG_SCORE,
           MIN(SCORE)       MIN_SCORE,
           MAX(SCORE)       MAX_SCORE,
           AVG(VIEW_COUNT)  AVG_VIEW_COUNT,
           MAX(VIEW_COUNT)  MAX_VIEW_COUNT,
           MIN(VIEW_COUNT)  MIN_VIEW_COUNT,
           SUM(VIEW_COUNT)  TOTAL_VIEW_COUNT,
           COUNT(*)         QUESTIONS_COUNT
    FROM FACT_QUESTIONS
    GROUP BY
        DIM_DATE_ID,
        DIM_TAG_TAG_2_ID
    UNION ALL
    -- аггрегация по 3-му тегу
    SELECT
           DIM_DATE_ID      DATE_ID,
           DIM_TAG_TAG_3_ID TAG_ID,
           AVG(SCORE)       AVG_SCORE,
           MIN(SCORE)       MIN_SCORE,
           MAX(SCORE)       MAX_SCORE,
           AVG(VIEW_COUNT)  AVG_VIEW_COUNT,
           MAX(VIEW_COUNT)  MAX_VIEW_COUNT,
           MIN(VIEW_COUNT)  MIN_VIEW_COUNT,
           SUM(VIEW_COUNT)  TOTAL_VIEW_COUNT,
           COUNT(*)         QUESTIONS_COUNT
    FROM FACT_QUESTIONS
    GROUP BY
        DIM_DATE_ID,
        DIM_TAG_TAG_3_ID
    UNION ALL
    -- аггрегация по 4-му тегу
    SELECT
           DIM_DATE_ID      DATE_ID,
           DIM_TAG_TAG_4_ID TAG_ID,
           AVG(SCORE)       AVG_SCORE,
           MIN(SCORE)       MIN_SCORE,
           MAX(SCORE)       MAX_SCORE,
           AVG(VIEW_COUNT)  AVG_VIEW_COUNT,
           MAX(VIEW_COUNT)  MAX_VIEW_COUNT,
           MIN(VIEW_COUNT)  MIN_VIEW_COUNT,
           SUM(VIEW_COUNT)  TOTAL_VIEW_COUNT,
           COUNT(*)         QUESTIONS_COUNT
    FROM FACT_QUESTIONS
    GROUP BY
        DIM_DATE_ID,
        DIM_TAG_TAG_4_ID
    UNION ALL
    -- аггрегация по 5-му тегу
    SELECT
           DIM_DATE_ID      DATE_ID,
           DIM_TAG_TAG_5_ID TAG_ID,
           AVG(SCORE)       AVG_SCORE,
           MIN(SCORE)       MIN_SCORE,
           MAX(SCORE)       MAX_SCORE,
           AVG(VIEW_COUNT)  AVG_VIEW_COUNT,
           MAX(VIEW_COUNT)  MAX_VIEW_COUNT,
           MIN(VIEW_COUNT)  MIN_VIEW_COUNT,
           SUM(VIEW_COUNT)  TOTAL_VIEW_COUNT,
           COUNT(*)         QUESTIONS_COUNT
    FROM FACT_QUESTIONS
    GROUP BY
        DIM_DATE_ID,
        DIM_TAG_TAG_5_ID
)
-- аггрегация данных уже сагрегированных по каждому из тегов отдельно,
-- чтобы получить независимо от позиции тега
SELECT DATE_ID,
       TAG_ID,
       SUM(AVG_SCORE * QUESTIONS_COUNT) / SUM(QUESTIONS_COUNT)      AVG_SCORE,
       MIN(MIN_SCORE)                                               MIN_SCORE,
       MAX(MAX_SCORE)                                               MAX_SCORE,
       SUM(AVG_VIEW_COUNT * QUESTIONS_COUNT) / SUM(QUESTIONS_COUNT) AVG_VIEW_COUNT,
       MAX(MAX_VIEW_COUNT)                                          MAX_VIEW_COUNT,
       MIN(MIN_VIEW_COUNT)                                          MIN_VIEW_COUNT,
       SUM(TOTAL_VIEW_COUNT)                                        TOTAL_VIEW_COUNT,
       SUM(QUESTIONS_COUNT)                                         QUESTIONS_COUNT
FROM STATS_FOR_ALL_TAGS
GROUP BY DATE_ID,
         TAG_ID;

-- создание таблицы аггрегатов по тегам и датам
create table AGG_QUESTIONS as
    with t as (
        select DAYS.DATE_ID                                                 DAY_ID,
               MONTHS.DATE_ID                                               MONTH_ID,
               YEARS.DATE_ID                                                YEAR_ID,
               TAG_ID,
               SUM(AVG_SCORE * QUESTIONS_COUNT) / SUM(QUESTIONS_COUNT)      AVG_SCORE,
               MIN(MIN_SCORE)                                               MIN_SCORE,
               MAX(MAX_SCORE)                                               MAX_SCORE,
               SUM(AVG_VIEW_COUNT * QUESTIONS_COUNT) / SUM(QUESTIONS_COUNT) AVG_VIEW_COUNT,
               MAX(MAX_VIEW_COUNT)                                          MAX_VIEW_COUNT,
               MIN(MIN_VIEW_COUNT)                                          MIN_VIEW_COUNT,
               SUM(TOTAL_VIEW_COUNT)                                        TOTAL_VIEW_COUNT,
               SUM(QUESTIONS_COUNT)                                         QUESTIONS_COUNT
        from STG_DATE_TAG_AGGS S
                 join DIM_DATE DAYS on (S.DATE_ID = DAYS.DATE_ID)
                 join DIM_DATE MONTHS on (trunc(DAYS."date", 'month') = MONTHS."date" and MONTHS."level" = 2)
                 join DIM_DATE YEARS on (trunc(DAYS."date", 'year') = YEARS."date" and YEARS."level" = 3)
        group by TAG_ID, ROLLUP ( YEARS.DATE_ID, MONTHS.DATE_ID, DAYS.DATE_ID )
    )
select
    -- переход обратно от отдельных id дня, месяца и года,
    -- к одному полю date_id, означающему наименьшее деление.
    -- Т.е. если запись является аггрегатом по дню, будет day_id,
    -- по месяцу -- month_id, по году year_id
    CASE WHEN
        DAY_ID IS NULL
    THEN (
        CASE WHEN
            MONTH_ID IS NULL
        THEN
            YEAR_ID
        ELSE
            MONTH_ID
        END)
    else
        DAY_ID
    end
        DATE_ID,
        TAG_ID,
        AVG_SCORE,
        MIN_SCORE,
        MAX_SCORE,
        AVG_VIEW_COUNT,
        MAX_VIEW_COUNT,
        MIN_VIEW_COUNT,
        TOTAL_VIEW_COUNT,
        QUESTIONS_COUNT
    from t;

