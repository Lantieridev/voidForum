import { register } from './authManager.js';

let isOpen = false;
let onCloseCallback = null;
let onSwitchToLoginCallback = null;

export function openRegisterModal(onClose = null, onSwitchToLogin = null) {
  isOpen = true;
  onCloseCallback = onClose;
  onSwitchToLoginCallback = onSwitchToLogin;
  render();
}

export function closeRegisterModal() {
  isOpen = false;
  onCloseCallback?.();
  onCloseCallback = null;
  onSwitchToLoginCallback = null;
  remove();
}

export function switchToLogin() {
  closeRegisterModal();
  onSwitchToLoginCallback?.();
}

function render() {
  remove();

  const overlay = document.createElement('div');
  overlay.className = 'modal-overlay';
  overlay.id = 'registerModalOverlay';
  overlay.innerHTML = `
    <div class="auth-modal">
      <div class="auth-modal-header">
        <h2>Crear Cuenta</h2>
        <button class="modal-close-btn" id="closeRegisterBtn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
          </svg>
        </button>
      </div>
      <form id="registerForm" class="auth-form">
        <div class="form-group">
          <label for="registerUsername">Usuario</label>
          <input type="text" id="registerUsername" name="username" required minlength="3" maxlength="20" autocomplete="username" />
          <span class="form-hint">Entre 3 y 20 caracteres</span>
        </div>
        <div class="form-group">
          <label for="registerEmail">Email</label>
          <input type="email" id="registerEmail" name="email" required autocomplete="email" />
        </div>
        <div class="form-group">
          <label for="registerPassword">Contraseña</label>
          <input type="password" id="registerPassword" name="password" required minlength="6" autocomplete="new-password" />
          <span class="form-hint">Mínimo 6 caracteres</span>
        </div>
        <div class="form-group">
          <label for="registerConfirmPassword">Confirmar Contraseña</label>
          <input type="password" id="registerConfirmPassword" name="confirmPassword" required autocomplete="new-password" />
        </div>
        <div class="form-error" id="registerError" style="display: none;"></div>
        <button type="submit" class="auth-submit-btn" id="registerSubmitBtn">
          <span class="btn-text">Crear Cuenta</span>
          <span class="btn-loader" style="display: none;">
            <svg class="spinner" viewBox="0 0 24 24">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.4" stroke-dashoffset="10"/>
            </svg>
          </span>
        </button>
      </form>
      <div class="auth-modal-footer">
        <p>¿Ya tenés cuenta? <a href="#" id="switchToLogin">Iniciar Sesión</a></p>
      </div>
    </div>
  `;

  document.body.appendChild(overlay);
  attachEvents();
}

function remove() {
  const existing = document.getElementById('registerModalOverlay');
  if (existing) {
    existing.remove();
  }
}

function attachEvents() {
  const overlay = document.getElementById('registerModalOverlay');
  const closeBtn = document.getElementById('closeRegisterBtn');
  const form = document.getElementById('registerForm');
  const switchLink = document.getElementById('switchToLogin');
  const submitBtn = document.getElementById('registerSubmitBtn');

  overlay?.addEventListener('click', (e) => {
    if (e.target === overlay) {
      closeRegisterModal();
    }
  });

  closeBtn?.addEventListener('click', closeRegisterModal);

  switchLink?.addEventListener('click', (e) => {
    e.preventDefault();
    switchToLogin();
  });

  form?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('registerUsername').value;
    const email = document.getElementById('registerEmail').value;
    const password = document.getElementById('registerPassword').value;
    const confirmPassword = document.getElementById('registerConfirmPassword').value;
    const errorDiv = document.getElementById('registerError');
    const btnText = submitBtn.querySelector('.btn-text');
    const btnLoader = submitBtn.querySelector('.btn-loader');

    if (password !== confirmPassword) {
      errorDiv.textContent = 'Las contraseñas no coinciden';
      errorDiv.style.display = 'block';
      return;
    }

    submitBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline';
    errorDiv.style.display = 'none';

    const result = await register(username, email, password);

    submitBtn.disabled = false;
    btnText.style.display = 'inline';
    btnLoader.style.display = 'none';

    if (result.success) {
      closeRegisterModal();
      if (typeof window.refreshUI === 'function') {
        window.refreshUI();
      }
    } else {
      errorDiv.textContent = result.error;
      errorDiv.style.display = 'block';
    }
  });
}

export function isRegisterModalOpen() {
  return isOpen;
}
