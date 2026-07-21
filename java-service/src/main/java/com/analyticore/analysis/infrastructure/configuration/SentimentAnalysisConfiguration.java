package com.analyticore.analysis.infrastructure.configuration;

import com.analyticore.analysis.domain.service.RuleBasedSentimentAnalyzer;
import com.analyticore.analysis.domain.service.SentimentAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura el analizador de sentimiento.
 */
@Configuration
public class SentimentAnalysisConfiguration {

    @Bean
    public SentimentAnalyzer sentimentAnalyzer() {
        return new RuleBasedSentimentAnalyzer();
    }
}