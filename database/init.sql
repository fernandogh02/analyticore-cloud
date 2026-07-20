-- =========================================================
-- AnalytiCore
-- Script inicial de la base de datos PostgreSQL
-- =========================================================

-- Tabla principal de trabajos de análisis
CREATE TABLE IF NOT EXISTS analysis_jobs (
    id UUID PRIMARY KEY,

    text_content TEXT NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',

    sentiment VARCHAR(20),

    keywords JSONB NOT NULL DEFAULT '[]'::jsonb,

    error_message TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_analysis_jobs_text
        CHECK (
            CHAR_LENGTH(TRIM(text_content)) >= 1
            AND CHAR_LENGTH(text_content) <= 5000
        ),

    CONSTRAINT chk_analysis_jobs_status
        CHECK (
            status IN (
                'PENDIENTE',
                'PROCESANDO',
                'COMPLETADO',
                'ERROR'
            )
        ),

    CONSTRAINT chk_analysis_jobs_sentiment
        CHECK (
            sentiment IS NULL
            OR sentiment IN (
                'POSITIVO',
                'NEGATIVO',
                'NEUTRAL'
            )
        ),

    CONSTRAINT chk_analysis_jobs_keywords
        CHECK (
            JSONB_TYPEOF(keywords) = 'array'
        )
);

-- Índice para consultar trabajos por estado
CREATE INDEX IF NOT EXISTS idx_analysis_jobs_status
    ON analysis_jobs(status);

-- Índice para ordenar y consultar trabajos por fecha
CREATE INDEX IF NOT EXISTS idx_analysis_jobs_created_at
    ON analysis_jobs(created_at);

-- Función para actualizar automáticamente updated_at
CREATE OR REPLACE FUNCTION update_analysis_jobs_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Recrear el trigger de forma segura
DROP TRIGGER IF EXISTS trg_analysis_jobs_updated_at
ON analysis_jobs;

CREATE TRIGGER trg_analysis_jobs_updated_at
BEFORE UPDATE ON analysis_jobs
FOR EACH ROW
EXECUTE FUNCTION update_analysis_jobs_updated_at();

-- Comentarios descriptivos
COMMENT ON TABLE analysis_jobs IS
'Trabajos de análisis de sentimiento y extracción de palabras clave';

COMMENT ON COLUMN analysis_jobs.id IS
'Identificador único del trabajo, utilizado como jobId';

COMMENT ON COLUMN analysis_jobs.text_content IS
'Texto enviado por el usuario';

COMMENT ON COLUMN analysis_jobs.status IS
'Estado: PENDIENTE, PROCESANDO, COMPLETADO o ERROR';

COMMENT ON COLUMN analysis_jobs.sentiment IS
'Resultado: POSITIVO, NEGATIVO o NEUTRAL';

COMMENT ON COLUMN analysis_jobs.keywords IS
'Lista JSON de palabras clave encontradas';

COMMENT ON COLUMN analysis_jobs.error_message IS
'Descripción de un error ocurrido durante el procesamiento';