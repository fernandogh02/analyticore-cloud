import { UI_STATUS } from '../../domain/constants/analysis'

const STATUS_CLASS_NAMES = {
  [UI_STATUS.IDLE]: 'status-chip--idle',
  [UI_STATUS.PENDING]: 'status-chip--pending',
  [UI_STATUS.PROCESSING]: 'status-chip--processing',
  [UI_STATUS.COMPLETED]: 'status-chip--completed',
  [UI_STATUS.ERROR]: 'status-chip--error',
}

export function StatusBadge({ status }) {
  const className = STATUS_CLASS_NAMES[status] ?? 'status-chip--idle'

  return (
    <span className={`status-chip ${className}`}>
      {status}
    </span>
  )
}