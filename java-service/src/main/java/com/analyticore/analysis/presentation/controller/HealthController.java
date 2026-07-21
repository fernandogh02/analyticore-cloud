package com.analyticore.analysis.presentation.controller;

import com.analyticore.analysis.presentation.dto.HealthResponse;
import com.analyticore.analysis.presentation.dto.RootResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rutas básicas para verificar el servicio.
 */
@RestController
public class HealthController {

    private static final String SERVICE_NAME =
        "java-service";

    private static final String VERSION =
        "0.1.0";

    /**
     * Devuelve información general de la aplicación.
     *
     * @return información del servicio
     */
    @GetMapping("/")
    public RootResponse root() {
        return new RootResponse(
            SERVICE_NAME,
            "AnalytiCore",
            VERSION,
            "Servicio Java funcionando correctamente."
        );
    }

    /**
     * Comprueba que el servicio esté activo.
     *
     * @return estado actual
     */
    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse(
            "UP",
            SERVICE_NAME
        );
    }
}