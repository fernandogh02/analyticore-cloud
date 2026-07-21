package com.analyticore.analysis.domain.service;

import com.analyticore.analysis.domain.model.Sentiment;
import com.analyticore.analysis.domain.model.SentimentAnalysisResult;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Analizador básico de sentimiento para textos en español.
 *
 * Utiliza vocabularios positivos y negativos,
 * negaciones e intensificadores.
 */
public class RuleBasedSentimentAnalyzer
    implements SentimentAnalyzer {

    private static final Set<String> POSITIVE_WORDS =
        Set.of(
            "alegre",
            "amable",
            "bien",
            "buena",
            "bueno",
            "correcta",
            "correcto",
            "eficiente",
            "encanta",
            "excelente",
            "facil",
            "feliz",
            "funciona",
            "genial",
            "gusta",
            "maravillosa",
            "maravilloso",
            "mejor",
            "perfecta",
            "perfecto",
            "positiva",
            "positivo",
            "rapida",
            "rapido",
            "recomiendo",
            "satisfecha",
            "satisfecho",
            "util"
        );

    private static final Set<String> NEGATIVE_WORDS =
        Set.of(
            "confusa",
            "confuso",
            "deficiente",
            "decepcionada",
            "decepcionado",
            "dificil",
            "error",
            "errores",
            "falla",
            "fallas",
            "fallo",
            "frustrante",
            "inutil",
            "lenta",
            "lento",
            "mal",
            "mala",
            "malo",
            "molesta",
            "molesto",
            "negativa",
            "negativo",
            "odio",
            "peor",
            "pesima",
            "pesimo",
            "problema",
            "problemas",
            "terrible",
            "triste"
        );

    private static final Set<String> NEGATIONS =
        Set.of(
            "jamas",
            "no",
            "nunca",
            "sin",
            "tampoco"
        );

    private static final Set<String> INTENSIFIERS =
        Set.of(
            "bastante",
            "demasiado",
            "extremadamente",
            "muy",
            "realmente",
            "sumamente"
        );

    private static final int NEGATION_DISTANCE = 2;

    @Override
    public SentimentAnalysisResult analyze(String text) {
        List<String> tokens = tokenize(text);

        int score = 0;
        List<String> evidence = new ArrayList<>();

        for (int index = 0; index < tokens.size(); index++) {
            String token = tokens.get(index);

            int wordValue = wordValue(token);

            if (wordValue == 0) {
                continue;
            }

            if (hasPreviousNegation(tokens, index)) {
                wordValue *= -1;
            }

            if (hasPreviousIntensifier(tokens, index)) {
                wordValue *= 2;
            }

            score += wordValue;
            evidence.add(token);
        }

        return new SentimentAnalysisResult(
            classify(score),
            score,
            evidence
        );
    }

    private int wordValue(String token) {
        if (POSITIVE_WORDS.contains(token)) {
            return 1;
        }

        if (NEGATIVE_WORDS.contains(token)) {
            return -1;
        }

        return 0;
    }

    private boolean hasPreviousNegation(
        List<String> tokens,
        int currentIndex
    ) {
        int startIndex = Math.max(
            0,
            currentIndex - NEGATION_DISTANCE
        );

        for (
            int index = startIndex;
            index < currentIndex;
            index++
        ) {
            if (NEGATIONS.contains(tokens.get(index))) {
                return true;
            }
        }

        return false;
    }

    private boolean hasPreviousIntensifier(
        List<String> tokens,
        int currentIndex
    ) {
        if (currentIndex == 0) {
            return false;
        }

        return INTENSIFIERS.contains(
            tokens.get(currentIndex - 1)
        );
    }

    private Sentiment classify(int score) {
        if (score > 0) {
            return Sentiment.POSITIVO;
        }

        if (score < 0) {
            return Sentiment.NEGATIVO;
        }

        return Sentiment.NEUTRAL;
    }

    private List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String normalized = Normalizer
            .normalize(
                text.toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD
            )
            .replaceAll("\\p{M}", "")
            .replaceAll("[^a-z0-9\\s]", " ")
            .trim();

        if (normalized.isBlank()) {
            return List.of();
        }

        return Arrays.stream(
                normalized.split("\\s+")
            )
            .filter(token -> !token.isBlank())
            .toList();
    }
}