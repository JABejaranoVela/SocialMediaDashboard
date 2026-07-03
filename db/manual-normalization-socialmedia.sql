-- MANUAL SCRIPT: DO NOT RUN WITHOUT A VERIFIED BACKUP.
-- Review every audit SELECT and the affected respondent IDs before BEGIN.
-- This script never changes the placeholder value 'string', never deletes respondents,
-- and never deletes platform or organization catalog rows.
-- Run against a maintenance copy first. Execute COMMIT manually only after verification.

-- 1. Preflight audit
SELECT gender, COUNT(*) AS total FROM respondent GROUP BY gender ORDER BY gender;
SELECT relationship_status, COUNT(*) AS total
FROM demographics GROUP BY relationship_status ORDER BY relationship_status;
SELECT occupation_status, COUNT(*) AS total
FROM demographics GROUP BY occupation_status ORDER BY occupation_status;
SELECT uses_social_media, COUNT(*) AS total
FROM social_media_usage GROUP BY uses_social_media ORDER BY uses_social_media;
SELECT daily_average_time, COUNT(*) AS total
FROM social_media_usage GROUP BY daily_average_time ORDER BY daily_average_time;
SELECT platform_id, platform_name FROM platform ORDER BY platform_name;
SELECT organization_id, organization_name FROM organization ORDER BY organization_name;

SELECT r.respondent_id, r.gender, d.relationship_status, d.occupation_status,
       smu.uses_social_media, smu.daily_average_time
FROM respondent r
LEFT JOIN demographics d ON d.respondent_id = r.respondent_id
LEFT JOIN social_media_usage smu ON smu.respondent_id = r.respondent_id
WHERE r.gender IN ('Masculino', 'Femenino', 'string')
   OR d.relationship_status IN ('Soltero', 'Casado', 'Divorciado', 'En relación', 'string')
   OR d.occupation_status IN (
       'Estudiante universitario', 'Estudiante escolar', 'Trabajador',
       'Trabajador asalariado', 'Jubilado', 'Desempleado', 'string')
   OR smu.uses_social_media IN ('Sí', 'Si', 'string')
   OR smu.daily_average_time IN (
       'Menos de 1 hora', 'Entre 1 y 2 horas', 'Entre 2 y 3 horas',
       'Entre 3 y 4 horas', 'Entre 4 y 5 horas', 'Más de 5 horas', 'string')
ORDER BY r.respondent_id;

-- Stop if this query returns rows: an organization link would collide with an existing target link.
SELECT ro.respondent_id, source.organization_name AS source_name, target.organization_name AS target_name
FROM respondent_organization ro
JOIN organization source ON source.organization_id = ro.organization_id
JOIN organization target ON target.organization_name = CASE source.organization_name
    WHEN 'Empresa' THEN 'Company'
    WHEN 'Gobierno' THEN 'Goverment'
    WHEN 'Colegio' THEN 'School'
    WHEN 'Universidad' THEN 'University'
    WHEN 'Privada' THEN 'Private'
END
JOIN respondent_organization existing
  ON existing.respondent_id = ro.respondent_id
 AND existing.organization_id = target.organization_id
WHERE source.organization_name IN ('Empresa', 'Gobierno', 'Colegio', 'Universidad', 'Privada');

-- 2. Mutations. Keep the transaction open until all postflight checks pass.
START TRANSACTION;

UPDATE respondent
SET gender = CASE gender
    WHEN 'Masculino' THEN 'Male'
    WHEN 'Femenino' THEN 'Female'
END
WHERE gender IN ('Masculino', 'Femenino');

UPDATE demographics
SET relationship_status = CASE relationship_status
    WHEN 'Soltero' THEN 'Single'
    WHEN 'Casado' THEN 'Married'
    WHEN 'Divorciado' THEN 'Divorced'
    WHEN 'En relación' THEN 'In a relationship'
END
WHERE relationship_status IN ('Soltero', 'Casado', 'Divorciado', 'En relación');

UPDATE demographics
SET occupation_status = CASE occupation_status
    WHEN 'Estudiante universitario' THEN 'University Student'
    WHEN 'Estudiante escolar' THEN 'School Student'
    WHEN 'Trabajador' THEN 'Salaried Worker'
    WHEN 'Trabajador asalariado' THEN 'Salaried Worker'
    WHEN 'Jubilado' THEN 'Retired'
    WHEN 'Desempleado' THEN 'Unemployed'
END
WHERE occupation_status IN (
    'Estudiante universitario', 'Estudiante escolar', 'Trabajador',
    'Trabajador asalariado', 'Jubilado', 'Desempleado');

UPDATE social_media_usage
SET uses_social_media = 'Yes'
WHERE uses_social_media IN ('Sí', 'Si');

UPDATE social_media_usage
SET daily_average_time = CASE daily_average_time
    WHEN 'Menos de 1 hora' THEN 'Less than an Hour'
    WHEN 'Entre 1 y 2 horas' THEN 'Between 1 and 2 hours'
    WHEN 'Entre 2 y 3 horas' THEN 'Between 2 and 3 hours'
    WHEN 'Entre 3 y 4 horas' THEN 'Between 3 and 4 hours'
    WHEN 'Entre 4 y 5 horas' THEN 'Between 4 and 5 hours'
    WHEN 'Más de 5 horas' THEN 'More than 5 hours'
END
WHERE daily_average_time IN (
    'Menos de 1 hora', 'Entre 1 y 2 horas', 'Entre 2 y 3 horas',
    'Entre 3 y 4 horas', 'Entre 4 y 5 horas', 'Más de 5 horas');

-- Execute only when the collision audit above returned zero rows and every target catalog exists.
UPDATE respondent_organization ro
JOIN organization source ON source.organization_id = ro.organization_id
JOIN organization target ON target.organization_name = CASE source.organization_name
    WHEN 'Empresa' THEN 'Company'
    WHEN 'Gobierno' THEN 'Goverment'
    WHEN 'Colegio' THEN 'School'
    WHEN 'Universidad' THEN 'University'
    WHEN 'Privada' THEN 'Private'
END
SET ro.organization_id = target.organization_id
WHERE source.organization_name IN ('Empresa', 'Gobierno', 'Colegio', 'Universidad', 'Privada');

-- 3. Postflight: Spanish aliases should be zero; 'string' remains for manual review.
SELECT relationship_status, COUNT(*) AS total
FROM demographics GROUP BY relationship_status ORDER BY relationship_status;
SELECT occupation_status, COUNT(*) AS total
FROM demographics GROUP BY occupation_status ORDER BY occupation_status;
SELECT uses_social_media, daily_average_time, COUNT(*) AS total
FROM social_media_usage GROUP BY uses_social_media, daily_average_time
ORDER BY uses_social_media, daily_average_time;
SELECT r.respondent_id, r.gender, d.relationship_status, d.occupation_status,
       smu.uses_social_media, smu.daily_average_time
FROM respondent r
LEFT JOIN demographics d ON d.respondent_id = r.respondent_id
LEFT JOIN social_media_usage smu ON smu.respondent_id = r.respondent_id
WHERE r.gender = 'string'
   OR d.relationship_status = 'string'
   OR d.occupation_status = 'string'
   OR smu.uses_social_media = 'string'
   OR smu.daily_average_time = 'string';

-- Choose exactly one after review:
-- COMMIT;
-- ROLLBACK;

-- Respondent 2 must be reviewed manually. Do not map its 'string' fields to Unknown.
