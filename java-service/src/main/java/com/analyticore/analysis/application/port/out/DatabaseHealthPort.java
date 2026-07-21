package com.analyticore.analysis.application.port.out;

/**
 * Operaciones requeridas para comprobar PostgreSQL.
 */
public interface DatabaseHealthPort {

    boolean canConnect();

    boolean analysisJobsTableExists();
}