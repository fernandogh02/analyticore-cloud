import {
  MAX_TEXT_LENGTH,
  MIN_TEXT_LENGTH,
} from '../constants/analysis.js'

const MIN_MEANINGFUL_CHARACTERS = 3

export function normalizeAnalysisText(text) {
  if (typeof text !== 'string') {
    return ''
  }

  return text
    .replace(/\s+/g, ' ')
    .trim()
}

export function validateAnalysisText(text) {
  const normalizedText =
    normalizeAnalysisText(text)

  if (!normalizedText) {
    return {
      isValid: false,
      normalizedText,
      error:
        'Escribe un comentario antes de iniciar el análisis.',
    }
  }

  if (
    normalizedText.length <
    MIN_TEXT_LENGTH
  ) {
    return {
      isValid: false,
      normalizedText,
      error:
        `El texto debe contener al menos ${MIN_TEXT_LENGTH} caracteres.`,
    }
  }

  if (
    normalizedText.length >
    MAX_TEXT_LENGTH
  ) {
    return {
      isValid: false,
      normalizedText,
      error:
        `El texto no puede superar ${MAX_TEXT_LENGTH} caracteres.`,
    }
  }

  const meaningfulCharacters =
    normalizedText.match(
      /[\p{L}\p{N}]/gu,
    ) ?? []

  if (
    meaningfulCharacters.length <
    MIN_MEANINGFUL_CHARACTERS
  ) {
    return {
      isValid: false,
      normalizedText,
      error:
        'El comentario debe contener al menos tres letras o números.',
    }
  }

  return {
    isValid: true,
    normalizedText,
    error: '',
  }
}