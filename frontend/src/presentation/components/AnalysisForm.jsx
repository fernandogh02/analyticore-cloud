import {
  MAX_TEXT_LENGTH,
  MIN_TEXT_LENGTH,
} from '../../domain/constants/analysis'

export function AnalysisForm({
  text,
  error,
  onTextChange,
  onSubmit,
}) {
  const remainingCharacters = MAX_TEXT_LENGTH - text.length
  const isSubmitDisabled = text.trim().length < MIN_TEXT_LENGTH

  return (
    <form className="analysis-form" onSubmit={onSubmit}>
      <div className="form-heading">
        <div>
          <p className="eyebrow">Nuevo análisis</p>
          <h1>Analiza un comentario</h1>
        </div>

        <span className="step-badge">Paso 1 de 1</span>
      </div>

      <p className="form-description">
        Ingresa un comentario para identificar su sentimiento y sus
        palabras más representativas.
      </p>

      <div className="form-field">
        <div className="form-label-row">
          <label htmlFor="analysis-text">Texto para analizar</label>

          <span
            className={
              remainingCharacters < 100
                ? 'character-counter character-counter--warning'
                : 'character-counter'
            }
          >
            {remainingCharacters} disponibles
          </span>
        </div>

        <textarea
          id="analysis-text"
          name="analysisText"
          value={text}
          rows="9"
          maxLength={MAX_TEXT_LENGTH}
          placeholder="Ejemplo: La plataforma es rápida, clara y muy fácil de utilizar."
          aria-describedby={
            error
              ? 'analysis-help analysis-error'
              : 'analysis-help'
          }
          aria-invalid={Boolean(error)}
          onChange={(event) => onTextChange(event.target.value)}
        />

        <p id="analysis-help" className="form-help">
          Escribe entre {MIN_TEXT_LENGTH} y {MAX_TEXT_LENGTH} caracteres.
        </p>

        {error && (
          <p id="analysis-error" className="form-error" role="alert">
            {error}
          </p>
        )}
      </div>

      <button
        className="primary-button"
        type="submit"
        disabled={isSubmitDisabled}
      >
        <span aria-hidden="true">✦</span>
        Preparar análisis
      </button>
    </form>
  )
}