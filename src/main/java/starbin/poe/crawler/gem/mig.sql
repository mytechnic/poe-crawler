SET @PrimeRegradingLensPrice = 130;
SET @SecondaryRegradingLensPrice = 450;
SET @DivineOrbPrice = 250;
TRUNCATE TABLE skill_gem_report;

SET @Level = 20;
SET @Quality = 20;
INSERT INTO skill_gem_report
SELECT
    a.*,
    basic_price * (basic_per / 100.0) - price - IF(LOCATE('보조', kor_name), @SecondaryRegradingLensPrice, @PrimeRegradingLensPrice) as basic_benefit,
    divergent_price * (divergent_per / 100.0) - price - IF(LOCATE('보조', kor_name), @SecondaryRegradingLensPrice, @PrimeRegradingLensPrice) as divergent_benefit,
    anomalous_price * (anomalous_per / 100.0) - price - IF(LOCATE('보조', kor_name), @SecondaryRegradingLensPrice, @PrimeRegradingLensPrice) as anomalous_benefit,
    phantasmal_price * (phantasmal_per / 100.0) - price - IF(LOCATE('보조', kor_name), @SecondaryRegradingLensPrice, @PrimeRegradingLensPrice) as phantasmal_benefit
FROM (
         SELECT
             a.kor_name,
             a.tag,
             a.weight,
             b.value * IF(b.value_type = 'Divine', @DivineOrbPrice, 1) price,
             b.listed,
             b.last7_day,
             b.level,
             b.quality,
             IFNULL(case when a.tag != '기본' then ROUND(c.basic_weight / (c.weight_total - a.weight) * 100, 2) end, 0) basic_per,
             IFNULL(case when a.tag != '상이한' then ROUND(c.divergent_weight / (c.weight_total - a.weight) * 100, 2) end, 0) divergent_per,
             IFNULL(case when a.tag != '기묘한' then ROUND(c.anomalous_weight / (c.weight_total - a.weight) * 100, 2) end, 0) anomalous_per,
             IFNULL(case when a.tag != '몽환적인' then ROUND(c.phantasmal_weight / (c.weight_total - a.weight) * 100, 2) end, 0) phantasmal_per,
             IFNULL((SELECT t.value * IF(t.value_type = 'Divine', @DivineOrbPrice, 1) FROM skill_gem_price t WHERE t.name = a.name and t.tag = '기본' AND t.level = @Level AND t.quality = @Quality AND t.is_corrupt = 0), 0) basic_price,
             IFNULL((SELECT t.value * IF(t.value_type = 'Divine', @DivineOrbPrice, 1) FROM skill_gem_price t WHERE t.name = a.name and t.tag = '상이한' AND t.level = @Level AND t.quality = @Quality AND t.is_corrupt = 0), 0) divergent_price,
             IFNULL((SELECT t.value * IF(t.value_type = 'Divine', @DivineOrbPrice, 1) FROM skill_gem_price t WHERE t.name = a.name and t.tag = '기묘한' AND t.level = @Level AND t.quality = @Quality AND t.is_corrupt = 0), 0) anomalous_price,
             IFNULL((SELECT t.value * IF(t.value_type = 'Divine', @DivineOrbPrice, 1) FROM skill_gem_price t WHERE t.name = a.name and t.tag = '몽환적인' AND t.level = @Level AND t.quality = @Quality AND t.is_corrupt = 0), 0) phantasmal_price,
             c.weight_total - a.weight as weight_total,
             d.total_price
         FROM skill_gem_weight a
                  left join skill_gem_price b on (a.name = b.name and a.tag = b.tag and b.tag != '각성한' AND b.level = @Level AND b.quality = @Quality AND b.is_corrupt = 0)
                  left join skill_gem c on (a.name = c.name)
                  left join (
             SELECT b.name, SUM(b.value * IF(b.value_type = 'Divine', @DivineOrbPrice, 1)) total_price
             FROM skill_gem_price b
             WHERE b.tag != '각성한' AND b.level = @Level AND b.quality = @Quality AND b.is_corrupt = 0
             GROUP BY b.name
         ) d on (a.name = d.name)
         WHERE b.tag != '각성한'
     ) a;

SET @Level = 19;
SET @Quality = 20;
INSERT INTO skill_gem_report
SELECT
    a.*,
    basic_price * (basic_per / 100.0) - price - IF(LOCATE('보조', kor_name), @SecondaryRegradingLensPrice, @PrimeRegradingLensPrice) as basic_benefit,
    divergent_price * (divergent_per / 100.0) - price - IF(LOCATE('보조', kor_name), @SecondaryRegradingLensPrice, @PrimeRegradingLensPrice) as divergent_benefit,
    anomalous_price * (anomalous_per / 100.0) - price - IF(LOCATE('보조', kor_name), @SecondaryRegradingLensPrice, @PrimeRegradingLensPrice) as anomalous_benefit,
    phantasmal_price * (phantasmal_per / 100.0) - price - IF(LOCATE('보조', kor_name), @SecondaryRegradingLensPrice, @PrimeRegradingLensPrice) as phantasmal_benefit
FROM (
         SELECT
             a.kor_name,
             a.tag,
             a.weight,
             b.value * IF(b.value_type = 'Divine', @DivineOrbPrice, 1) price,
             b.listed,
             b.last7_day,
             b.level,
             b.quality,
             IFNULL(case when a.tag != '기본' then ROUND(c.basic_weight / (c.weight_total - a.weight) * 100, 2) end, 0) basic_per,
             IFNULL(case when a.tag != '상이한' then ROUND(c.divergent_weight / (c.weight_total - a.weight) * 100, 2) end, 0) divergent_per,
             IFNULL(case when a.tag != '기묘한' then ROUND(c.anomalous_weight / (c.weight_total - a.weight) * 100, 2) end, 0) anomalous_per,
             IFNULL(case when a.tag != '몽환적인' then ROUND(c.phantasmal_weight / (c.weight_total - a.weight) * 100, 2) end, 0) phantasmal_per,
             IFNULL((SELECT t.value * IF(t.value_type = 'Divine', @DivineOrbPrice, 1) FROM skill_gem_price t WHERE t.name = a.name and t.tag = '기본' AND t.level = @Level AND t.quality = @Quality AND t.is_corrupt = 0), 0) basic_price,
             IFNULL((SELECT t.value * IF(t.value_type = 'Divine', @DivineOrbPrice, 1) FROM skill_gem_price t WHERE t.name = a.name and t.tag = '상이한' AND t.level = @Level AND t.quality = @Quality AND t.is_corrupt = 0), 0) divergent_price,
             IFNULL((SELECT t.value * IF(t.value_type = 'Divine', @DivineOrbPrice, 1) FROM skill_gem_price t WHERE t.name = a.name and t.tag = '기묘한' AND t.level = @Level AND t.quality = @Quality AND t.is_corrupt = 0), 0) anomalous_price,
             IFNULL((SELECT t.value * IF(t.value_type = 'Divine', @DivineOrbPrice, 1) FROM skill_gem_price t WHERE t.name = a.name and t.tag = '몽환적인' AND t.level = @Level AND t.quality = @Quality AND t.is_corrupt = 0), 0) phantasmal_price,
             c.weight_total - a.weight as weight_total,
             d.total_price
         FROM skill_gem_weight a
                  left join skill_gem_price b on (a.name = b.name and a.tag = b.tag and b.tag != '각성한' AND b.level = @Level AND b.quality = @Quality AND b.is_corrupt = 0)
                  left join skill_gem c on (a.name = c.name)
                  left join (
             SELECT b.name, SUM(b.value * IF(b.value_type = 'Divine', @DivineOrbPrice, 1)) total_price
             FROM skill_gem_price b
             WHERE b.tag != '각성한' AND b.level = @Level AND b.quality = @Quality AND b.is_corrupt = 0
             GROUP BY b.name
         ) d on (a.name = d.name)
         WHERE b.tag != '각성한'
     ) a;

SELECT kor_name, tag, price, listed, last7_day, level, quality, basic_benefit
FROM skill_gem_report
WHERE basic_benefit > 0
ORDER BY basic_benefit DESC;

SELECT kor_name, tag, price, listed, last7_day, level, quality, divergent_benefit
FROM skill_gem_report
WHERE divergent_benefit > 0
ORDER BY divergent_benefit DESC;

SELECT kor_name, tag, price, listed, last7_day, level, quality, anomalous_benefit
FROM skill_gem_report
WHERE anomalous_benefit > 0
ORDER BY anomalous_benefit DESC;

SELECT kor_name, tag, price, listed, last7_day, level, quality, phantasmal_benefit
FROM skill_gem_report
WHERE phantasmal_benefit > 0
ORDER BY phantasmal_benefit DESC;