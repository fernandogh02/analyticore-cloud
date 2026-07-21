export function AnalysisPreview({ submittedText }) {
  if (!submittedText) {
    return (
      <section className="preview-card preview-card--empty">
        <div className="preview-icon" aria-hidden="true">
          ✦
        </div>

        <h2>Resultado del análisis</h2>

        <p>
          Aquí aparecerán el sentimiento, el estado y las palabras clave
          cuando el frontend se conecte con el servicio Python.
        </p>

        <div className="preview-steps">
          <span>1. Enviar texto</span>
          <span>2. Procesar</span>
          <span>3. Mostrar resultado</span>
        </div>
      </section>
    )
  }

  return (
    <section className="preview-card">
      <div className="result-heading">
        <div>
          <p className="eyebrow">Vista previa local</p>
          <h2>Texto preparado</h2>
        </div>

        <span className="status-chip status-chip--preview">
          SIN ENVIAR
        </span>
      </div>

      <div className="submitted-text">
        <p>{submittedText}</p>
      </div>

      <div className="information-message">
        <span aria-hidden="true">ⓘ</span>

        <p>
          El texto solamente se guardó en el estado local de React.
          La conexión con Python se incorporará en el siguiente paso.
        </p>
      </div>
    </section>
  )
}