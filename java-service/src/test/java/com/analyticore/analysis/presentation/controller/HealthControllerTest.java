package com.analyticore.analysis.presentation.controller;

import com.analyticore.analysis.presentation.dto.HealthResponse;
import com.analyticore.analysis.presentation.dto.RootResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas unitarias del controlador básico.
 */
class HealthControllerTest {

    private final HealthController controller =
        new HealthController();

    @Test
    void healthReturnsServiceStatus() {
        HealthResponse response = controller.health();

        assertEquals("UP", response.status());
        assertEquals("java-service", response.service());
    }

    @Test
    void rootReturnsApplicationInformation() {
        RootResponse response = controller.root();

        assertEquals(
            "java-service",
            response.service()
        );

        assertEquals(
            "AnalytiCore",
            response.application()
        );

        assertEquals(
            "0.1.0",
            response.version()
        );
    }
}