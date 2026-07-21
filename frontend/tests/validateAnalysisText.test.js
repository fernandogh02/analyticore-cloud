import assert from 'node:assert/strict'
import test from 'node:test'

import {
  normalizeAnalysisText,
  validateAnalysisText,
} from '../src/domain/validation/validateAnalysisText.js'

test(
  'rechaza un texto vacío',
  () => {
    const result =
      validateAnalysisText('   ')

    assert.equal(
      result.isValid,
      false,
    )
  },
)

test(
  'rechaza un texto demasiado corto',
  () => {
    const result =
      validateAnalysisText('Hola')

    assert.equal(
      result.isValid,
      false,
    )
  },
)

test(
  'rechaza únicamente signos',
  () => {
    const result =
      validateAnalysisText(
        '!!!!!!!!!!!!',
      )

    assert.equal(
      result.isValid,
      false,
    )
  },
)

test(
  'acepta un comentario válido',
  () => {
    const result =
      validateAnalysisText(
        'La plataforma funciona correctamente.',
      )

    assert.equal(
      result.isValid,
      true,
    )
  },
)

test(
  'normaliza espacios repetidos',
  () => {
    const normalized =
      normalizeAnalysisText(
        '  La   plataforma   funciona.  ',
      )

    assert.equal(
      normalized,
      'La plataforma funciona.',
    )
  },
)

test(
  'rechaza más de 2000 caracteres',
  () => {
    const result =
      validateAnalysisText(
        'a'.repeat(2001),
      )

    assert.equal(
      result.isValid,
      false,
    )
  },
)