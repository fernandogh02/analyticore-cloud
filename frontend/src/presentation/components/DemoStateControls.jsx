import { UI_STATUS } from '../../domain/constants/analysis'

export function DemoStateControls({
  status,
  onStatusChange,
}) {
  if (!import.meta.env.DEV) {
    return null
  }

  const statuses = Object.values(UI_STATUS)

  return (
    <section className="demo-controls">
      <div>
        <p className="eyebrow">Modo de desarrollo</p>
        <h2>Probar estados visuales</h2>
      </div>

      <div className="demo-controls__buttons">
        {statuses.map((availableStatus) => (
          <button
            className={
              status === availableStatus
                ? 'demo-button demo-button--active'
                : 'demo-button'
            }
            type="button"
            key={availableStatus}
            onClick={() =>
              onStatusChange(availableStatus)
            }
          >
            {availableStatus}
          </button>
        ))}
      </div>
    </section>
  )
}