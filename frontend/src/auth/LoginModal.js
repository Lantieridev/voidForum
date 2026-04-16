import { login } from './authManager.js';

let isOpen = false;
let onCloseCallback = null;
let onSwitchToRegisterCallback = null;

export function openLoginModal(onClose = null, onSwitchToRegister = null) {
  isOpen = true;
  onCloseCallback = onClose;
  onSwitchToRegisterCallback = onSwitchToRegister;
  render();
}

export function closeLoginModal() {
  isOpen = false;
  onCloseCallback?.();
  onCloseCallback = null;
  onSwitchToRegisterCallback = null;
  remove();
}

export function switchToRegister() {
  const callback = onSwitchToRegisterCallback;
  closeLoginModal();
  callback?.();
}

function render() {
  remove();

  const overlay = document.createElement('div');
  overlay.className = 'modal-overlay';
  overlay.id = 'loginModalOverlay';
  overlay.innerHTML = `
    <div class="auth-modal">
      <div class="auth-modal-header">
        <h2>Iniciar Sesión</h2>
        <button class="modal-close-btn" id="closeLoginBtn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
          </svg>
        </button>
      </div>
      <form id="loginForm" class="auth-form">
        <div class="form-group">
          <label for="loginUsername">Usuario</label>
          <input type="text" id="loginUsername" name="username" required autocomplete="username" />
        </div>
        <div class="form-group">
          <label for="loginPassword">Contraseña</label>
          <input type="password" id="loginPassword" name="password" required autocomplete="current-password" />
        </div>
        <div class="form-error" id="loginError" style="display: none;"></div>
        <button type="submit" class="auth-submit-btn" id="loginSubmitBtn">
          <span class="btn-text">Iniciar Sesión</span>
          <span class="btn-loader" style="display: none;">
            <svg class="spinner" viewBox="0 0 24 24">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.4" stroke-dashoffset="10"/>
            </svg>
          </span>
        </button>
      </form>
      <div class="auth-modal-footer">
        <p>¿No tenés cuenta? <a href="#" id="switchToRegister">Registrarse</a></p>
      </div>
    </div>
  `;

  document.body.appendChild(overlay);
  attachEvents();
}

function remove() {
  const existing = document.getElementById('loginModalOverlay');
  if (existing) {
    existing.remove();
  }
}

function attachEvents() {
  const overlay = document.getElementById('loginModalOverlay');
  const closeBtn = document.getElementById('closeLoginBtn');
  const form = document.getElementById('loginForm');
  const switchLink = document.getElementById('switchToRegister');
  const submitBtn = document.getElementById('loginSubmitBtn');

  overlay?.addEventListener('click', (e) => {
    if (e.target === overlay) {
      closeLoginModal();
    }
  });

  closeBtn?.addEventListener('click', closeLoginModal);

  switchLink?.addEventListener('click', (e) => {
    e.preventDefault();
    switchToRegister();
  });

  form?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    const errorDiv = document.getElementById('loginError');
    const btnText = submitBtn.querySelector('.btn-text');
    const btnLoader = submitBtn.querySelector('.btn-loader');

    submitBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline';
    errorDiv.style.display = 'none';

    const result = await login(username, password);

    submitBtn.disabled = false;
    btnText.style.display = 'inline';
    btnLoader.style.display = 'none';

    if (result.success) {
      closeLoginModal();
      if (typeof window.refreshUI === 'function') {
        window.refreshUI();
      }
    } else {
      errorDiv.textContent = result.error;
      errorDiv.style.display = 'block';
    }
  });
}

export function isLoginModalOpen() {
  return isOpen;
}
