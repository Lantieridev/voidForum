import { usersApi, ApiError } from '../auth/api.js';
import { init as initAuth } from '../auth/authManager.js';

let isOpen = false;
let onCloseCallback = null;
let activeTab = 'profile';

export function openSettingsModal(onClose = null) {
  isOpen = true;
  activeTab = 'profile';
  onCloseCallback = onClose;
  render();
}

export function closeSettingsModal() {
  isOpen = false;
  onCloseCallback?.();
  onCloseCallback = null;
  remove();
}

function remove() {
  const existing = document.getElementById('settingsModalOverlay');
  if (existing) {
    existing.remove();
  }
}

async function render() {
  remove();

  const user = window.currentUser || {};

  const overlay = document.createElement('div');
  overlay.className = 'modal-overlay';
  overlay.id = 'settingsModalOverlay';
  overlay.innerHTML = `
    <div class="settings-modal">
      <div class="settings-modal-header">
        <h2>Configuración</h2>
        <button class="modal-close-btn" id="closeSettingsBtn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
          </svg>
        </button>
      </div>
      <div class="settings-tabs">
        <button class="settings-tab ${activeTab === 'profile' ? 'active' : ''}" data-tab="profile">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
            <circle cx="12" cy="7" r="4"/>
          </svg>
          Perfil
        </button>
        <button class="settings-tab ${activeTab === 'security' ? 'active' : ''}" data-tab="security">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
          </svg>
          Seguridad
        </button>
        <button class="settings-tab ${activeTab === 'notifications' ? 'active' : ''}" data-tab="notifications">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
          </svg>
          Notificaciones
        </button>
        <button class="settings-tab ${activeTab === 'danger' ? 'active' : ''}" data-tab="danger">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/>
            <line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
          Zona de Peligro
        </button>
      </div>
      <div class="settings-content">
        ${renderProfileTab(user)}
        ${renderSecurityTab()}
        ${renderNotificationsTab(user)}
        ${renderDangerTab()}
      </div>
    </div>
  `;

  document.body.appendChild(overlay);
  attachEvents();
}

function renderProfileTab(user) {
  const displayName = user.displayName || '';
  const username = user.username || '';
  const email = user.email || '';
  const bio = user.bio || '';

  return `
    <div class="settings-tab-content ${activeTab === 'profile' ? 'active' : ''}" id="tab-profile">
      <h3>Información del Perfil</h3>
      <form id="profileForm" class="settings-form">
        <div class="form-group">
          <label for="profileDisplayName">Nombre a mostrar</label>
          <input type="text" id="profileDisplayName" value="${escapeHtml(displayName)}" placeholder="Tu nombre" maxlength="50" />
          <span class="char-counter"><span id="displayNameCount">${displayName.length}</span>/50</span>
        </div>
        <div class="form-group">
          <label for="profileUsername">Usuario</label>
          <input type="text" id="profileUsername" value="${escapeHtml(username)}" required minlength="3" maxlength="20" pattern="[a-zA-Z0-9_]+" />
          <span class="form-hint">Solo letras, números y guiones bajos</span>
        </div>
        <div class="form-group">
          <label for="profileEmail">Email</label>
          <input type="email" id="profileEmail" value="${escapeHtml(email)}" required />
        </div>
        <div class="form-group">
          <label for="profileBio">Biografía</label>
          <textarea id="profileBio" placeholder="Cuéntanos sobre ti..." maxlength="280">${escapeHtml(bio)}</textarea>
          <span class="char-counter"><span id="bioCount">${bio.length}</span>/280</span>
        </div>
        <div class="form-error" id="profileError" style="display: none;"></div>
        <div class="form-success" id="profileSuccess" style="display: none;"></div>
        <button type="submit" class="settings-submit-btn" id="profileSubmitBtn">
          <span class="btn-text">Guardar cambios</span>
          <span class="btn-loader" style="display: none;">
            <svg class="spinner" viewBox="0 0 24 24">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.4" stroke-dashoffset="10"/>
            </svg>
          </span>
        </button>
      </form>
    </div>
  `;
}

function renderSecurityTab() {
  return `
    <div class="settings-tab-content ${activeTab === 'security' ? 'active' : ''}" id="tab-security">
      <h3>Cambiar Contraseña</h3>
      <form id="passwordForm" class="settings-form">
        <div class="form-group">
          <label for="currentPassword">Contraseña actual</label>
          <div class="password-input-wrapper">
            <input type="password" id="currentPassword" required autocomplete="current-password" />
            <button type="button" class="password-toggle-btn" onclick="window.togglePasswordVisibility('currentPassword', 'currentPasswordToggle')" id="currentPasswordToggle">${window.icons?.eyeClosed || ''}</button>
          </div>
        </div>
        <div class="form-group">
          <label for="newPassword">Nueva contraseña</label>
          <div class="password-input-wrapper">
            <input type="password" id="newPassword" required minlength="6" autocomplete="new-password" />
            <button type="button" class="password-toggle-btn" onclick="window.togglePasswordVisibility('newPassword', 'newPasswordToggle')" id="newPasswordToggle">${window.icons?.eyeClosed || ''}</button>
          </div>
          <span class="form-hint">Mínimo 6 caracteres</span>
        </div>
        <div class="form-group">
          <label for="confirmPassword">Confirmar nueva contraseña</label>
          <div class="password-input-wrapper">
            <input type="password" id="confirmPassword" required autocomplete="new-password" />
            <button type="button" class="password-toggle-btn" onclick="window.togglePasswordVisibility('confirmPassword', 'confirmPasswordToggle')" id="confirmPasswordToggle">${window.icons?.eyeClosed || ''}</button>
          </div>
        </div>
        <div class="form-error" id="passwordError" style="display: none;"></div>
        <div class="form-success" id="passwordSuccess" style="display: none;"></div>
        <button type="submit" class="settings-submit-btn" id="passwordSubmitBtn">
          <span class="btn-text">Actualizar contraseña</span>
          <span class="btn-loader" style="display: none;">
            <svg class="spinner" viewBox="0 0 24 24">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.4" stroke-dashoffset="10"/>
            </svg>
          </span>
        </button>
      </form>
    </div>
  `;
}

function renderNotificationsTab(user) {
  return `
    <div class="settings-tab-content ${activeTab === 'notifications' ? 'active' : ''}" id="tab-notifications">
      <h3>Preferencias de Notificaciones</h3>
      <form id="notificationsForm" class="settings-form">
        <div class="toggle-group">
          <div class="toggle-item">
            <div class="toggle-info">
              <span class="toggle-label">Likes en mis posts</span>
              <span class="toggle-description">Notificar cuando alguien le da like a tus posts</span>
            </div>
            <label class="toggle-switch">
              <input type="checkbox" id="notifyLikes" ${user.notifyLikes !== false ? 'checked' : ''} />
              <span class="toggle-slider"></span>
            </label>
          </div>
          <div class="toggle-item">
            <div class="toggle-info">
              <span class="toggle-label">Comentarios</span>
              <span class="toggle-description">Notificar cuando alguien comenta en tus posts</span>
            </div>
            <label class="toggle-switch">
              <input type="checkbox" id="notifyComments" ${user.notifyComments !== false ? 'checked' : ''} />
              <span class="toggle-slider"></span>
            </label>
          </div>
          <div class="toggle-item">
            <div class="toggle-info">
              <span class="toggle-label">Menciones</span>
              <span class="toggle-description">Notificar cuando alguien te menciona</span>
            </div>
            <label class="toggle-switch">
              <input type="checkbox" id="notifyMentions" ${user.notifyMentions !== false ? 'checked' : ''} />
              <span class="toggle-slider"></span>
            </label>
          </div>
        </div>
        <div class="form-error" id="notificationsError" style="display: none;"></div>
        <div class="form-success" id="notificationsSuccess" style="display: none;"></div>
        <button type="submit" class="settings-submit-btn" id="notificationsSubmitBtn">
          <span class="btn-text">Guardar preferencias</span>
          <span class="btn-loader" style="display: none;">
            <svg class="spinner" viewBox="0 0 24 24">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.4" stroke-dashoffset="10"/>
            </svg>
          </span>
        </button>
      </form>
    </div>
  `;
}

function renderDangerTab() {
  return `
    <div class="settings-tab-content ${activeTab === 'danger' ? 'active' : ''}" id="tab-danger">
      <h3>Zona de Peligro</h3>
      <div class="danger-zone">
        <div class="danger-item">
          <div class="danger-info">
            <span class="danger-label">Eliminar cuenta</span>
            <span class="danger-description">Esta acción eliminará tu cuenta de forma permanente. Tus posts se mantendrán pero aparecerán como "[deleted]". Esta acción no se puede deshacer.</span>
          </div>
          <button type="button" class="danger-btn" id="deleteAccountBtn">Eliminar cuenta</button>
        </div>
      </div>
      <div id="deleteConfirmSection" style="display: none;" class="delete-confirm">
        <p>¿Estás seguro? Esta acción es irreversible.</p>
        <div class="form-group">
          <label for="deletePassword">Ingresá tu contraseña para confirmar</label>
          <input type="password" id="deletePassword" required autocomplete="current-password" />
        </div>
        <div class="form-error" id="deleteError" style="display: none;"></div>
        <div class="danger-actions">
          <button type="button" class="cancel-btn" id="cancelDeleteBtn">Cancelar</button>
          <button type="button" class="confirm-delete-btn" id="confirmDeleteBtn">
            <span class="btn-text">Sí, eliminar mi cuenta</span>
            <span class="btn-loader" style="display: none;">
              <svg class="spinner" viewBox="0 0 24 24">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.4" stroke-dashoffset="10"/>
              </svg>
            </span>
          </button>
        </div>
      </div>
    </div>
  `;
}

function escapeHtml(text) {
  if (!text) return '';
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}

function showToast(message, type = 'success') {
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.textContent = message;
  document.body.appendChild(toast);

  setTimeout(() => toast.classList.add('show'), 10);
  setTimeout(() => {
    toast.classList.remove('show');
    setTimeout(() => toast.remove(), 300);
  }, 3000);
}

async function attachEvents() {
  const overlay = document.getElementById('settingsModalOverlay');
  const closeBtn = document.getElementById('closeSettingsBtn');
  const tabs = document.querySelectorAll('.settings-tab');

  overlay?.addEventListener('click', (e) => {
    if (e.target === overlay) {
      closeSettingsModal();
    }
  });

  closeBtn?.addEventListener('click', closeSettingsModal);

  tabs?.forEach(tab => {
    tab.addEventListener('click', () => {
      activeTab = tab.dataset.tab;
      document.querySelectorAll('.settings-tab').forEach(t => t.classList.remove('active'));
      document.querySelectorAll('.settings-tab-content').forEach(c => c.classList.remove('active'));
      tab.classList.add('active');
      document.getElementById(`tab-${activeTab}`)?.classList.add('active');
    });
  });

  const displayNameInput = document.getElementById('profileDisplayName');
  const bioInput = document.getElementById('profileBio');
  const displayNameCount = document.getElementById('displayNameCount');
  const bioCount = document.getElementById('bioCount');

  displayNameInput?.addEventListener('input', () => {
    displayNameCount.textContent = displayNameInput.value.length;
  });

  bioInput?.addEventListener('input', () => {
    bioCount.textContent = bioInput.value.length;
  });

  attachProfileForm();
  attachPasswordForm();
  attachNotificationsForm();
  attachDeleteAccount();
}

function attachProfileForm() {
  const form = document.getElementById('profileForm');
  const submitBtn = document.getElementById('profileSubmitBtn');
  const errorDiv = document.getElementById('profileError');
  const successDiv = document.getElementById('profileSuccess');

  form?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const btnText = submitBtn.querySelector('.btn-text');
    const btnLoader = submitBtn.querySelector('.btn-loader');

    submitBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline';
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    try {
      const data = {
        displayName: document.getElementById('profileDisplayName').value,
        username: document.getElementById('profileUsername').value,
        email: document.getElementById('profileEmail').value,
        bio: document.getElementById('profileBio').value,
      };

      await usersApi.updateProfile(data);

      await initAuth();
      if (typeof window.refreshUI === 'function') {
        window.refreshUI();
      }

      successDiv.textContent = 'Perfil actualizado correctamente';
      successDiv.style.display = 'block';
      showToast('Perfil actualizado');

    } catch (error) {
      errorDiv.textContent = error.message || 'Error al actualizar el perfil';
      errorDiv.style.display = 'block';
    } finally {
      submitBtn.disabled = false;
      btnText.style.display = 'inline';
      btnLoader.style.display = 'none';
    }
  });
}

function attachPasswordForm() {
  const form = document.getElementById('passwordForm');
  const submitBtn = document.getElementById('passwordSubmitBtn');
  const errorDiv = document.getElementById('passwordError');
  const successDiv = document.getElementById('passwordSuccess');

  form?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (newPassword !== confirmPassword) {
      errorDiv.textContent = 'Las contraseñas no coinciden';
      errorDiv.style.display = 'block';
      return;
    }

    const btnText = submitBtn.querySelector('.btn-text');
    const btnLoader = submitBtn.querySelector('.btn-loader');

    submitBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline';
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    try {
      await usersApi.changePassword(
        document.getElementById('currentPassword').value,
        newPassword
      );

      form.reset();
      successDiv.textContent = 'Contraseña actualizada correctamente';
      successDiv.style.display = 'block';
      showToast('Contraseña actualizada');

    } catch (error) {
      errorDiv.textContent = error.message || 'Error al cambiar la contraseña';
      errorDiv.style.display = 'block';
    } finally {
      submitBtn.disabled = false;
      btnText.style.display = 'inline';
      btnLoader.style.display = 'none';
    }
  });
}

function attachNotificationsForm() {
  const form = document.getElementById('notificationsForm');
  const submitBtn = document.getElementById('notificationsSubmitBtn');
  const errorDiv = document.getElementById('notificationsError');
  const successDiv = document.getElementById('notificationsSuccess');

  form?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const btnText = submitBtn.querySelector('.btn-text');
    const btnLoader = submitBtn.querySelector('.btn-loader');

    submitBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline';
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    try {
      const prefs = {
        notifyLikes: document.getElementById('notifyLikes').checked,
        notifyComments: document.getElementById('notifyComments').checked,
        notifyMentions: document.getElementById('notifyMentions').checked,
      };

      await usersApi.updateNotifications(prefs);
      await initAuth();

      successDiv.textContent = 'Preferencias guardadas';
      successDiv.style.display = 'block';
      showToast('Notificaciones actualizadas');

    } catch (error) {
      errorDiv.textContent = error.message || 'Error al guardar las preferencias';
      errorDiv.style.display = 'block';
    } finally {
      submitBtn.disabled = false;
      btnText.style.display = 'inline';
      btnLoader.style.display = 'none';
    }
  });
}

function attachDeleteAccount() {
  const deleteBtn = document.getElementById('deleteAccountBtn');
  const confirmSection = document.getElementById('deleteConfirmSection');
  const cancelBtn = document.getElementById('cancelDeleteBtn');
  const confirmBtn = document.getElementById('confirmDeleteBtn');
  const errorDiv = document.getElementById('deleteError');

  deleteBtn?.addEventListener('click', () => {
    deleteBtn.style.display = 'none';
    confirmSection.style.display = 'block';
  });

  cancelBtn?.addEventListener('click', () => {
    confirmSection.style.display = 'none';
    deleteBtn.style.display = 'block';
    document.getElementById('deletePassword').value = '';
    errorDiv.style.display = 'none';
  });

  confirmBtn?.addEventListener('click', async () => {
    const password = document.getElementById('deletePassword').value;
    const btnText = confirmBtn.querySelector('.btn-text');
    const btnLoader = confirmBtn.querySelector('.btn-loader');

    confirmBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline';
    errorDiv.style.display = 'none';

    try {
      await usersApi.deleteAccount(password);
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.reload();

    } catch (error) {
      errorDiv.textContent = error.message || 'Error al eliminar la cuenta';
      errorDiv.style.display = 'block';
      confirmBtn.disabled = false;
      btnText.style.display = 'inline';
      btnLoader.style.display = 'none';
    }
  });
}

export function isSettingsModalOpen() {
  return isOpen;
}
