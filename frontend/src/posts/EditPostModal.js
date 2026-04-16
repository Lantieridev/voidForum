import { postsApi } from '../auth/api.js';

let isOpen = false;
let currentPost = null;
let onCloseCallback = null;

const POPULAR_TAGS = ['java', 'javascript', 'python', 'spring', 'react', 'angular', 'vue', 'nodejs', 'mongodb', 'mysql', 'docker', 'kubernetes', 'git', 'api', 'backend', 'frontend', 'fullstack', 'devops', 'cloud', 'aws'];

export function openEditPostModal(post, onClose = null) {
  isOpen = true;
  currentPost = post;
  onCloseCallback = onClose;
  render();
}

export function closeEditPostModal() {
  isOpen = false;
  currentPost = null;
  onCloseCallback?.();
  onCloseCallback = null;
  remove();
}

function render() {
  remove();

  const overlay = document.createElement('div');
  overlay.className = 'modal-overlay';
  overlay.id = 'editPostModalOverlay';
  
  const tagsString = (currentPost.tags || []).map(t => `#${t}`).join(' ');

  overlay.innerHTML = `
    <div class="post-modal">
      <div class="post-modal-header">
        <h2>Editar Post</h2>
        <button class="modal-close-btn" id="closeEditPostBtn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
          </svg>
        </button>
      </div>
      <form id="editPostForm" class="post-form">
        <div class="form-group">
          <label for="editPostContent">Contenido</label>
          <textarea id="editPostContent" name="content" required placeholder="Escribe tu post aquí..." rows="5">${currentPost.content || ''}</textarea>
        </div>
        <div class="form-group">
          <label for="editPostTags">Tags</label>
          <div class="tags-input-container">
            <input type="text" id="editPostTags" name="tags" placeholder="#java, #spring, #backend" value="${tagsString}" autocomplete="off" />
            <div class="tags-suggestions" id="editTagsSuggestions"></div>
          </div>
          <div class="tags-preview" id="editTagsPreview"></div>
        </div>
        <div class="form-error" id="editPostError" style="display: none;"></div>
        <button type="submit" class="post-submit-btn" id="editPostSubmitBtn">
          <span class="btn-text">Guardar Cambios</span>
          <span class="btn-loader" style="display: none;">
            <svg class="spinner" viewBox="0 0 24 24">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.4" stroke-dashoffset="10"/>
            </svg>
          </span>
        </button>
      </form>
    </div>
  `;

  document.body.appendChild(overlay);
  attachEvents();
  updateTagsPreview();
}

function remove() {
  const existing = document.getElementById('editPostModalOverlay');
  if (existing) {
    existing.remove();
  }
}

function attachEvents() {
  const overlay = document.getElementById('editPostModalOverlay');
  const closeBtn = document.getElementById('closeEditPostBtn');
  const form = document.getElementById('editPostForm');
  const tagsInput = document.getElementById('editPostTags');
  const submitBtn = document.getElementById('editPostSubmitBtn');

  overlay?.addEventListener('click', (e) => {
    if (e.target === overlay) {
      closeEditPostModal();
    }
  });

  closeBtn?.addEventListener('click', closeEditPostModal);

  tagsInput?.addEventListener('input', (e) => {
    const input = e.target;
    const value = input.value;
    
    const parts = value.split(' ');
    const lastPart = parts[parts.length - 1];
    
    if (lastPart.startsWith('#')) {
      const search = lastPart.slice(1).toLowerCase();
      const suggestions = POPULAR_TAGS.filter(tag => 
        tag.toLowerCase().includes(search) && 
        !getSelectedTags().includes(tag)
      );
      showSuggestions(suggestions);
    } else if (lastPart === '' && parts.length > 1) {
      const previousPart = parts[parts.length - 2];
      if (previousPart.startsWith('#') && previousPart.length > 1) {
        const tagToAdd = previousPart.slice(1).trim();
        if (tagToAdd && !getSelectedTags().includes(tagToAdd)) {
          const tags = getSelectedTags();
          tags.push(tagToAdd);
          input.value = tags.map(t => `#${t}`).join(' ') + ' ';
          updateTagsPreview();
          hideSuggestions();
        }
      }
    } else {
      hideSuggestions();
    }
    
    updateTagsPreview();
  });

  tagsInput?.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      const suggestion = document.querySelector('.tag-suggestion.selected');
      if (suggestion) {
        addTag(suggestion.dataset.tag);
      }
    }
  });

  form?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const content = document.getElementById('editPostContent').value.trim();
    const tagsInputValue = document.getElementById('editPostTags').value;
    const tags = parseTags(tagsInputValue);
    const errorDiv = document.getElementById('editPostError');
    const btnText = submitBtn.querySelector('.btn-text');
    const btnLoader = submitBtn.querySelector('.btn-loader');

    if (!content) {
      errorDiv.textContent = 'El contenido es requerido';
      errorDiv.style.display = 'block';
      return;
    }

    submitBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline';
    errorDiv.style.display = 'none';

    try {
      await postsApi.update(currentPost.id, content, tags);
      closeEditPostModal();
      if (typeof window.refreshPosts === 'function') {
        window.refreshPosts();
      }
    } catch (error) {
      errorDiv.textContent = error.message || 'Error al editar el post';
      errorDiv.style.display = 'block';
    } finally {
      submitBtn.disabled = false;
      btnText.style.display = 'inline';
      btnLoader.style.display = 'none';
    }
  });
}

function showSuggestions(suggestions) {
  const container = document.getElementById('editTagsSuggestions');
  if (!container || suggestions.length === 0) {
    hideSuggestions();
    return;
  }

  container.innerHTML = suggestions.map((tag, index) => `
    <div class="tag-suggestion ${index === 0 ? 'selected' : ''}" data-tag="${tag}">
      #${tag}
    </div>
  `).join('');

  container.querySelectorAll('.tag-suggestion').forEach(el => {
    el.addEventListener('click', () => {
      addTag(el.dataset.tag);
    });
  });

  container.style.display = 'block';
}

function hideSuggestions() {
  const container = document.getElementById('editTagsSuggestions');
  if (container) {
    container.style.display = 'none';
  }
}

function addTag(tag) {
  const input = document.getElementById('editPostTags');
  const tags = getSelectedTags();
  
  if (!tags.includes(tag)) {
    tags.push(tag);
    input.value = tags.map(t => `#${t}`).join(' ') + ' ';
  }
  
  updateTagsPreview();
  hideSuggestions();
  input.focus();
}

function getSelectedTags() {
  const input = document.getElementById('editPostTags');
  if (!input) return [];
  
  return input.value
    .split(' ')
    .map(t => t.trim().replace(/^#/, ''))
    .filter(t => t.length > 0);
}

function parseTags(input) {
  return input
    .split(' ')
    .map(t => t.trim().replace(/^#/, '').toLowerCase())
    .filter(t => t.length > 0);
}

function updateTagsPreview() {
  const preview = document.getElementById('editTagsPreview');
  const tags = getSelectedTags();
  
  if (tags.length === 0) {
    preview.innerHTML = '';
    return;
  }
  
  preview.innerHTML = tags.map(tag => `<span class="tag-preview">#${tag}</span>`).join('');
}

export function isEditPostModalOpen() {
  return isOpen;
}
