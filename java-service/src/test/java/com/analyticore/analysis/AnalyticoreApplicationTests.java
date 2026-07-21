package com.analyticore.analysis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Comprueba que Spring pueda iniciar la aplicación.
 */
@SpringBootTest
@ActiveProfiles("test")
class AnalyticoreApplicationTests {

    @Test
    void contextLoads() {
        // Aprueba si el contexto inicia.
    }
}