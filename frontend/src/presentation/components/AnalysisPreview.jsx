import {
  SENTIMENT,
  UI_STATUS,
} from '../../domain/constants/analysis'
import { StatusBadge } from './StatusBadge'

function getSentimentClassName(sentiment) {
  if (sentiment === SENTIMENT.POSITIVE) {
    return 'sentiment-badge--positive'
  }

  if (sentiment === SENTIMENT.NEGATIVE) {
    return 'sentiment-badge--negative'
  }

  return 'sentiment-badge--neutral'
}

export function AnalysisPreview({
  status,
  submittedText,
  sentiment,
  keywords,
  errorMessage,
}) {
  if (status === UI_STATUS.IDLE) {
    return (
      <section className="preview-card preview-card--empty">
        <div className="preview-icon" aria-hidden="true">
          ✦
        </div>

        <h2>Resultado del análisis</h2>

        <p>
          Aquí aparecerán el sentimiento, el estado y las palabras
          clave del comentario.
        </p>

        <div className="preview-steps">
          <span>1. Enviar texto</span>
          <span>2. Procesar</span>
          <span>3. Mostrar resultado</span>
        </div>
      </section>
    )
  }

  if (
    status === UI_STATUS.PENDING ||
    status === UI_STATUS.PROCESSING
  ) {
    return (
      <section
        className="preview-card processing-card"
        aria-live="polite"
      >
        <div className="result-heading">
          <div>
            <p className="eyebrow">Análisis en curso</p>
            <h2>Procesando comentario</h2>
          </div>

          <StatusBadge status={status} />
        </div>

        <div className="processing-content">
          <div
            className="loading-spinner"
            aria-hidden="true"
          />

          <h3>
            {status === UI_STATUS.PENDING
              ? 'Preparando el trabajo'
              : 'Analizando el contenido'}
          </h3>

          <p>
            {status === UI_STATUS.PENDING
              ? 'El comentario fue recibido y está esperando procesamiento.'
              : 'Se está calculando el sentimiento y extrayendo las palabras clave.'}
          </p>
        </div>

        <div className="submitted-text">
          <p>{submittedText}</p>
        </div>
      </section>
    )
  }

  if (status === UI_STATUS.ERROR) {
    return (
      <section
        className="preview-card error-card"
        aria-live="assertive"
      >
        <div className="result-heading">
          <div>
            <p className="eyebrow">Problema detectado</p>
            <h2>No se completó el análisis</h2>
          </div>

          <StatusBadge status={status} />
        </div>

        <div className="error-message">
          <span className="error-message__icon" aria-hidden="true">
            !
          </span>

          <div>
            <strong>Ocurrió un error</strong>
            <p>
              {errorMessage ||
                'No fue posible comunicarse con el servicio de análisis.'}
            </p>
          </div>
        </div>

        {submittedText && (
          <div className="submitted-text">
            <p>{submittedText}</p>
          </div>
        )}
      </section>
    )
  }

  return (
    <section
      className="preview-card completed-card"
      aria-live="polite"
    >
      <div className="result-heading">
        <div>
          <p className="eyebrow">Resultado final</p>
          <h2>Análisis completado</h2>
        </div>

        <StatusBadge status={status} />
      </div>

      <div className="result-section">
        <span className="result-label">
          Sentimiento identificado
        </span>

        <span
          className={`sentiment-badge ${getSentimentClassName(
            sentiment,
          )}`}
        >
          {sentiment}
        </span>
      </div>

      <div className="result-section">
        <span className="result-label">
          Palabras clave
        </span>

        {keywords.length > 0 ? (
          <div className="keyword-list">
            {keywords.map((keyword) => (
              <span className="keyword-chip" key={keyword}>
                {keyword}
              </span>
            ))}
          </div>
        ) : (
          <p className="empty-result">
            No se identificaron palabras clave.
          </p>
        )}
      </div>

      <div className="result-section">
        <span className="result-label">
          Texto analizado
        </span>

        <div className="submitted-text">
          <p>{submittedText}</p>
        </div>
      </div>
    </section>
  )
}