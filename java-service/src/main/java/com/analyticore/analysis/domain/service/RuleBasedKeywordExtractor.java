package com.analyticore.analysis.domain.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Extrae palabras clave mediante frecuencia,
 * normalización y eliminación de palabras comunes.
 */
public class RuleBasedKeywordExtractor
    implements KeywordExtractor {

    private static final int MAX_KEYWORDS = 5;
    private static final int MIN_KEYWORD_LENGTH = 4;

    /**
     * Palabras comunes que no aportan significado
     * como palabras clave.
     */
    private static final Set<String> STOP_WORDS =
        Set.of(
            "a",
            "al",
            "algo",
            "algun",
            "alguna",
            "algunas",
            "alguno",
            "algunos",
            "ante",
            "como",
            "con",
            "contra",
            "cual",
            "cuando",
            "cuatro",
            "de",
            "del",
            "desde",
            "donde",
            "dos",
            "el",
            "ella",
            "ellas",
            "ellos",
            "en",
            "entre",
            "era",
            "es",
            "esa",
            "esas",
            "ese",
            "esos",
            "esta",
            "estas",
            "este",
            "estos",
            "fue",
            "ha",
            "hay",
            "la",
            "las",
            "lo",
            "los",
            "mas",
            "mi",
            "mis",
            "mucha",
            "muchas",
            "mucho",
            "muchos",
            "muy",
            "no",
            "o",
            "para",
            "pero",
            "por",
            "porque",
            "que",
            "se",
            "sin",
            "sobre",
            "su",
            "sus",
            "tambien",
            "te",
            "tiene",
            "todo",
            "tres",
            "un",
            "una",
            "unas",
            "uno",
            "unos",
            "y",
            "ya"
        );

    @Override
    public List<String> extract(String text) {
        List<String> tokens = tokenize(text);

        if (tokens.isEmpty()) {
            return List.of();
        }

        Map<String, WordStatistics> statistics =
            new HashMap<>();

        for (int index = 0; index < tokens.size(); index++) {
            String token = tokens.get(index);

            if (!isKeywordCandidate(token)) {
                continue;
            }

            WordStatistics current =
                statistics.get(token);

            if (current == null) {
                statistics.put(
                    token,
                    new WordStatistics(1, index)
                );
            } else {
                current.increment();
            }
        }

        List<Map.Entry<String, WordStatistics>> entries =
            new ArrayList<>(statistics.entrySet());

        entries.sort((left, right) -> {
            int frequencyComparison = Integer.compare(
                right.getValue().getCount(),
                left.getValue().getCount()
            );

            if (frequencyComparison != 0) {
                return frequencyComparison;
            }

            int positionComparison = Integer.compare(
                left.getValue().getFirstPosition(),
                right.getValue().getFirstPosition()
            );

            if (positionComparison != 0) {
                return positionComparison;
            }

            return left.getKey().compareTo(
                right.getKey()
            );
        });

        return entries.stream()
            .limit(MAX_KEYWORDS)
            .map(Map.Entry::getKey)
            .toList();
    }

    private boolean isKeywordCandidate(String token) {
        if (token.length() < MIN_KEYWORD_LENGTH) {
            return false;
        }

        if (STOP_WORDS.contains(token)) {
            return false;
        }

        return !token.matches("\\d+");
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

    /**
     * Conserva la frecuencia y posición inicial.
     */
    private static class WordStatistics {

        private int count;
        private final int firstPosition;

        WordStatistics(
            int count,
            int firstPosition
        ) {
            this.count = count;
            this.firstPosition = firstPosition;
        }

        void increment() {
            count++;
        }

        int getCount() {
            return count;
        }

        int getFirstPosition() {
            return firstPosition;
        }
    }
}