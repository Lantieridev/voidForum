import { openLoginModal } from './LoginModal.js';
import { openRegisterModal } from './RegisterModal.js';

let currentCard = null;

export function showRequireAuthCard(action = 'esta acción') {
  if (currentCard) {
    currentCard.remove();
  }

  const overlay = document.createElement('div');
  overlay.className = 'require-auth-overlay';
  overlay.id = 'requireAuthOverlay';
  overlay.innerHTML = `
    <div class="require-auth-card">
      <div class="require-auth-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
          <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
        </svg>
      </div>
      <h3>Iniciar sesión requerido</h3>
      <p>Para ${action}, necesitás tener una cuenta en VoidForum.</p>
      <div class="require-auth-actions">
        <button class="require-auth-btn primary" id="requireAuthLogin">
          Iniciar Sesión
        </button>
        <button class="require-auth-btn secondary" id="requireAuthRegister">
          Crear Cuenta
        </button>
      </div>
      <button class="require-auth-close" id="requireAuthClose">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M18 6L6 18M6 6l12 12"/>
        </svg>
      </button>
    </div>
  `;

  document.body.appendChild(overlay);
  currentCard = overlay;

  const closeCard = () => {
    if (currentCard) {
      currentCard.remove();
      currentCard = null;
    }
  };

  document.getElementById('requireAuthLogin')?.addEventListener('click', () => {
    closeCard();
    openLoginModal();
  });

  document.getElementById('requireAuthRegister')?.addEventListener('click', () => {
    closeCard();
    openRegisterModal();
  });

  document.getElementById('requireAuthClose')?.addEventListener('click', closeCard);

  overlay?.addEventListener('click', (e) => {
    if (e.target === overlay) {
      closeCard();
    }
  });
}

export function closeRequireAuthCard() {
  if (currentCard) {
    currentCard.remove();
    currentCard = null;
  }
}
