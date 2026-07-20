-- =========================================================
-- Pruebas de la base de datos AnalytiCore
-- =========================================================

-- Limpiar únicamente el registro utilizado en esta prueba
DELETE FROM analysis_jobs
WHERE id = '11111111-1111-4111-8111-111111111111';

-- 1. Crear un trabajo pendiente
INSERT INTO analysis_jobs (
    id,
    text_content
)
VALUES (
    '11111111-1111-4111-8111-111111111111',
    'La plataforma funciona muy bien y es fácil de utilizar.'
);

-- Consultar el trabajo pendiente
SELECT
    id,
    text_content,
    status,
    sentiment,
    keywords,
    created_at,
    updated_at
FROM analysis_jobs
WHERE id = '11111111-1111-4111-8111-111111111111';

-- 2. Cambiar el trabajo a PROCESANDO
UPDATE analysis_jobs
SET status = 'PROCESANDO'
WHERE id = '11111111-1111-4111-8111-111111111111';

-- Consultar el trabajo en procesamiento
SELECT
    id,
    status,
    updated_at
FROM analysis_jobs
WHERE id = '11111111-1111-4111-8111-111111111111';

-- 3. Guardar los resultados
UPDATE analysis_jobs
SET
    status = 'COMPLETADO',
    sentiment = 'POSITIVO',
    keywords = '[
        "plataforma",
        "funciona",
        "fácil"
    ]'::jsonb
WHERE id = '11111111-1111-4111-8111-111111111111';

-- Consultar el resultado final
SELECT
    id,
    text_content,
    status,
    sentiment,
    keywords,
    error_message,
    created_at,
    updated_at
FROM analysis_jobs
WHERE id = '11111111-1111-4111-8111-111111111111';

-- Eliminar el registro de prueba
DELETE FROM analysis_jobs
WHERE id = '11111111-1111-4111-8111-111111111111';