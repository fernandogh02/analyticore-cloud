package com.analyticore.analysis.infrastructure.configuration;

import com.analyticore.analysis.domain.service.KeywordExtractor;
import com.analyticore.analysis.domain.service.RuleBasedKeywordExtractor;
import com.analyticore.analysis.domain.service.RuleBasedSentimentAnalyzer;
import com.analyticore.analysis.domain.service.SentimentAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura los componentes de análisis de texto.
 */
@Configuration
public class SentimentAnalysisConfiguration {

    @Bean
    public SentimentAnalyzer sentimentAnalyzer() {
        return new RuleBasedSentimentAnalyzer();
    }

    @Bean
    public KeywordExtractor keywordExtractor() {
        return new RuleBasedKeywordExtractor();
    }
}