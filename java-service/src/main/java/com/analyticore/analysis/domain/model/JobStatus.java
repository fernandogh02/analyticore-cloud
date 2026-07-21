package com.analyticore.analysis.domain.model;

/**
 * Estados posibles de un trabajo de análisis.
 */
public enum JobStatus {
    PENDIENTE,
    PROCESANDO,
    COMPLETADO,
    ERROR
}