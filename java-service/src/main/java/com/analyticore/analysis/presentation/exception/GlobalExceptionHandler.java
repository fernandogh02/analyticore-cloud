package com.analyticore.analysis.presentation.exception;

import com.analyticore.analysis.domain.exception.AnalysisJobNotFoundException;
import com.analyticore.analysis.domain.exception.InvalidJobStateException;
import com.analyticore.analysis.presentation.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Convierte excepciones en respuestas HTTP seguras.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
        AnalysisJobNotFoundException.class
    )
    public ResponseEntity<ApiErrorResponse>
        handleJobNotFound() {

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                new ApiErrorResponse(
                    "JOB_NOT_FOUND",
                    "No se encontró el trabajo solicitado."
                )
            );
    }

    @ExceptionHandler(
        InvalidJobStateException.class
    )
    public ResponseEntity<ApiErrorResponse>
        handleInvalidJobState() {

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                new ApiErrorResponse(
                    "INVALID_JOB_STATE",
                    "El trabajo no puede procesarse "
                        + "desde su estado actual."
                )
            );
    }

    @ExceptionHandler(
        MethodArgumentNotValidException.class
    )
    public ResponseEntity<ApiErrorResponse>
        handleValidationError() {

        return ResponseEntity
            .badRequest()
            .body(
                new ApiErrorResponse(
                    "INVALID_REQUEST",
                    "La solicitud contiene datos inválidos."
                )
            );
    }

    @ExceptionHandler(
        HttpMessageNotReadableException.class
    )
    public ResponseEntity<ApiErrorResponse>
        handleUnreadableRequest() {

        return ResponseEntity
            .badRequest()
            .body(
                new ApiErrorResponse(
                    "INVALID_REQUEST",
                    "La solicitud contiene datos inválidos."
                )
            );
    }
}