package com.analyticore.analysis.presentation.controller;

import com.analyticore.analysis.application.usecase.StartAnalysisResult;
import com.analyticore.analysis.application.usecase.StartAnalysisUseCase;
import com.analyticore.analysis.presentation.dto.StartAnalysisRequest;
import com.analyticore.analysis.presentation.dto.StartAnalysisResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint utilizado exclusivamente por el servicio Python.
 */
@RestController
@RequestMapping("/internal")
public class InternalAnalysisController {

    private final StartAnalysisUseCase useCase;

    public InternalAnalysisController(
        StartAnalysisUseCase useCase
    ) {
        this.useCase = useCase;
    }

    /**
     * Acepta un trabajo para iniciar su procesamiento.
     *
     * @param request solicitud con jobId
     * @return respuesta 202
     */
    @PostMapping("/analysis")
    public ResponseEntity<StartAnalysisResponse>
        startAnalysis(
            @Valid
            @RequestBody
            StartAnalysisRequest request
        ) {

        StartAnalysisResult result =
            useCase.execute(request.jobId());

        StartAnalysisResponse response =
            new StartAnalysisResponse(
                result.jobId(),
                result.status(),
                "El análisis fue completado correctamente."
            );

        return ResponseEntity
            .accepted()
            .body(response);
    }
}