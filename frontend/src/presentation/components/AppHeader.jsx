export function AppHeader() {
  return (
    <header className="app-header">
      <div className="app-header__content">
        <a className="brand" href="/" aria-label="Página principal de AnalytiCore">
          <span className="brand__icon" aria-hidden="true">
            A
          </span>

          <span>
            <strong className="brand__name">AnalytiCore</strong>
            <span className="brand__description">
              Análisis inteligente de comentarios
            </span>
          </span>
        </a>

        <span className="environment-badge">Entorno local</span>
      </div>
    </header>
  )
}