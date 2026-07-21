package com.analyticore.analysis.domain.service;

import java.util.List;

/**
 * Contrato para extraer palabras clave de un texto.
 */
public interface KeywordExtractor {

    /**
     * Extrae las palabras más representativas.
     *
     * @param text texto que será procesado
     * @return lista ordenada de palabras clave
     */
    List<String> extract(String text);
}