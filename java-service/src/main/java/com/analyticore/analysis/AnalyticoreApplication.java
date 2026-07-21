package com.analyticore.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del servicio Java de AnalytiCore.
 */
@SpringBootApplication
public class AnalyticoreApplication {

    private AnalyticoreApplication() {
        // Evita instancias innecesarias.
    }

    public static void main(String[] args) {
        SpringApplication.run(
            AnalyticoreApplication.class,
            args
        );
    }
}