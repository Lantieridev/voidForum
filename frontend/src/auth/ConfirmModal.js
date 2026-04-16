let onConfirmCallback = null;
let onCancelCallback = null;

export function openConfirmModal(message, onConfirm = null, onCancel = null) {
  onConfirmCallback = onConfirm;
  onCancelCallback = onCancel;
  render(message);
}

function closeConfirmModal(confirmed = false) {
  if (confirmed) {
    onConfirmCallback?.();
  } else {
    onCancelCallback?.();
  }
  onConfirmCallback = null;
  onCancelCallback = null;
  remove();
}

function render(message) {
  remove();

  const overlay = document.createElement('div');
  overlay.className = 'modal-overlay confirm-modal-overlay';
  overlay.id = 'confirmModalOverlay';
  overlay.innerHTML = `
    <div class="confirm-modal">
      <div class="confirm-modal-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
        </svg>
      </div>
      <p class="confirm-modal-message">${message}</p>
      <div class="confirm-modal-actions">
        <button class="confirm-modal-cancel" id="confirmCancelBtn">Cancelar</button>
        <button class="confirm-modal-confirm" id="confirmOkBtn">Eliminar</button>
      </div>
    </div>
  `;

  document.body.appendChild(overlay);
  attachEvents(overlay);
}

function remove() {
  const existing = document.getElementById('confirmModalOverlay');
  if (existing) {
    existing.remove();
  }
}

function attachEvents(overlay) {
  const cancelBtn = document.getElementById('confirmCancelBtn');
  const confirmBtn = document.getElementById('confirmOkBtn');

  overlay?.addEventListener('click', (e) => {
    if (e.target === overlay) {
      closeConfirmModal(false);
    }
  });

  cancelBtn?.addEventListener('click', () => closeConfirmModal(false));
  confirmBtn?.addEventListener('click', () => closeConfirmModal(true));
}
