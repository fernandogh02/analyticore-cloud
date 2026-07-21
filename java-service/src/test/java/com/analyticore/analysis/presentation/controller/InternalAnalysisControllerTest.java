package com.analyticore.analysis.presentation.controller;

import com.analyticore.analysis.application.usecase.StartAnalysisResult;
import com.analyticore.analysis.application.usecase.StartAnalysisUseCase;
import com.analyticore.analysis.domain.exception.AnalysisJobNotFoundException;
import com.analyticore.analysis.domain.exception.InvalidJobStateException;
import com.analyticore.analysis.domain.model.JobStatus;
import com.analyticore.analysis.presentation.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas HTTP del endpoint interno.
 */
class InternalAnalysisControllerTest {

    private StartAnalysisUseCase useCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        useCase = mock(
            StartAnalysisUseCase.class
        );

        InternalAnalysisController controller =
            new InternalAnalysisController(useCase);

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(
                new GlobalExceptionHandler()
            )
            .build();
    }

    @Test
    void acceptsValidJob() throws Exception {
        UUID jobId = UUID.randomUUID();

        when(useCase.execute(jobId))
            .thenReturn(
                new StartAnalysisResult(
                    jobId,
                    JobStatus.COMPLETADO
                )
            );

        mockMvc.perform(
                post("/internal/analysis")
                    .contentType(
                        MediaType.APPLICATION_JSON
                    )
                    .content(
                        """
                        {
                          "jobId": "%s"
                        }
                        """.formatted(jobId)
                    )
            )
            .andExpect(status().isAccepted())
            .andExpect(
                jsonPath("$.jobId")
                    .value(jobId.toString())
            )
            .andExpect(
                jsonPath("$.status")
                    .value("COMPLETADO")
            );
    }

    @Test
    void rejectsMissingJobId() throws Exception {
        mockMvc.perform(
                post("/internal/analysis")
                    .contentType(
                        MediaType.APPLICATION_JSON
                    )
                    .content("{}")
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                jsonPath("$.error")
                    .value("INVALID_REQUEST")
            );
    }

    @Test
    void rejectsMalformedUuid() throws Exception {
        mockMvc.perform(
                post("/internal/analysis")
                    .contentType(
                        MediaType.APPLICATION_JSON
                    )
                    .content(
                        """
                        {
                          "jobId": "no-es-un-uuid"
                        }
                        """
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                jsonPath("$.error")
                    .value("INVALID_REQUEST")
            );
    }

    @Test
    void returnsNotFound() throws Exception {
        UUID jobId = UUID.randomUUID();

        when(useCase.execute(any(UUID.class)))
            .thenThrow(
                new AnalysisJobNotFoundException(
                    jobId
                )
            );

        mockMvc.perform(
                post("/internal/analysis")
                    .contentType(
                        MediaType.APPLICATION_JSON
                    )
                    .content(
                        """
                        {
                          "jobId": "%s"
                        }
                        """.formatted(jobId)
                    )
            )
            .andExpect(status().isNotFound())
            .andExpect(
                jsonPath("$.error")
                    .value("JOB_NOT_FOUND")
            );
    }

    @Test
    void returnsConflictForInvalidState()
        throws Exception {

        UUID jobId = UUID.randomUUID();

        when(useCase.execute(any(UUID.class)))
            .thenThrow(
                new InvalidJobStateException(
                    jobId,
                    JobStatus.COMPLETADO
                )
            );

        mockMvc.perform(
                post("/internal/analysis")
                    .contentType(
                        MediaType.APPLICATION_JSON
                    )
                    .content(
                        """
                        {
                          "jobId": "%s"
                        }
                        """.formatted(jobId)
                    )
            )
            .andExpect(status().isConflict())
            .andExpect(
                jsonPath("$.error")
                    .value("INVALID_JOB_STATE")
            );
    }
}