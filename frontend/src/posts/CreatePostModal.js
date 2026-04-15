import { postsApi } from '../auth/api.js';

let isOpen = false;
let onCloseCallback = null;

const POPULAR_TAGS = ['java', 'javascript', 'python', 'spring', 'react', 'angular', 'vue', 'nodejs', 'mongodb', 'mysql', 'docker', 'kubernetes', 'git', 'api', 'backend', 'frontend', 'fullstack', 'devops', 'cloud', 'aws'];

export function openCreatePostModal(onClose = null) {
  isOpen = true;
  onCloseCallback = onClose;
  render();
}

export function closeCreatePostModal() {
  isOpen = false;
  onCloseCallback?.();
  onCloseCallback = null;
  remove();
}

function render() {
  remove();

  const overlay = document.createElement('div');
  overlay.className = 'modal-overlay';
  overlay.id = 'createPostModalOverlay';
  overlay.innerHTML = `
    <div class="post-modal">
      <div class="post-modal-header">
        <h2>Crear Post</h2>
        <button class="modal-close-btn" id="closeCreatePostBtn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
          </svg>
        </button>
      </div>
      <form id="createPostForm" class="post-form">
        <div class="form-group">
          <label for="postContent">¿Qué estás pensando?</label>
          <textarea id="postContent" name="content" required placeholder="Escribe tu post aquí..." rows="5"></textarea>
        </div>
        <div class="form-group">
          <label for="postTags">Tags</label>
          <div class="tags-input-container">
            <input type="text" id="postTags" name="tags" placeholder="#java, #spring, #backend" autocomplete="off" />
            <div class="tags-suggestions" id="tagsSuggestions"></div>
          </div>
          <div class="tags-preview" id="tagsPreview"></div>
        </div>
        <div class="form-error" id="createPostError" style="display: none;"></div>
        <button type="submit" class="post-submit-btn" id="createPostSubmitBtn">
          <span class="btn-text">Publicar</span>
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
}

function remove() {
  const existing = document.getElementById('createPostModalOverlay');
  if (existing) {
    existing.remove();
  }
}

function attachEvents() {
  const overlay = document.getElementById('createPostModalOverlay');
  const closeBtn = document.getElementById('closeCreatePostBtn');
  const form = document.getElementById('createPostForm');
  const tagsInput = document.getElementById('postTags');
  const submitBtn = document.getElementById('createPostSubmitBtn');

  overlay?.addEventListener('click', (e) => {
    if (e.target === overlay) {
      closeCreatePostModal();
    }
  });

  closeBtn?.addEventListener('click', closeCreatePostModal);

  tagsInput?.addEventListener('input', (e) => {
    const value = e.target.value;
    const lastTag = value.split(',').pop().trim();
    
    if (lastTag.startsWith('#')) {
      const search = lastTag.slice(1).toLowerCase();
      const suggestions = POPULAR_TAGS.filter(tag => 
        tag.toLowerCase().includes(search) && 
        !getSelectedTags().includes(tag)
      );
      showSuggestions(suggestions);
    } else {
      hideSuggestions();
    }
    
    updateTagsPreview();
  });

  tagsInput?.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      const suggestions = document.querySelector('.tag-suggestion.selected');
      if (suggestions) {
        addTag(suggestions.dataset.tag);
      }
    }
  });

  form?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const content = document.getElementById('postContent').value.trim();
    const tagsInputValue = document.getElementById('postTags').value;
    const tags = parseTags(tagsInputValue);
    const errorDiv = document.getElementById('createPostError');
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
      await postsApi.create(content, tags);
      closeCreatePostModal();
      if (typeof window.refreshPosts === 'function') {
        window.refreshPosts();
      }
    } catch (error) {
      errorDiv.textContent = error.message || 'Error al crear el post';
      errorDiv.style.display = 'block';
    } finally {
      submitBtn.disabled = false;
      btnText.style.display = 'inline';
      btnLoader.style.display = 'none';
    }
  });
}

function showSuggestions(suggestions) {
  const container = document.getElementById('tagsSuggestions');
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
  const container = document.getElementById('tagsSuggestions');
  if (container) {
    container.style.display = 'none';
  }
}

function addTag(tag) {
  const input = document.getElementById('postTags');
  const tags = getSelectedTags();
  
  if (!tags.includes(tag)) {
    tags.push(tag);
    input.value = tags.map(t => `#${t}`).join(', ') + (tags.length > 0 ? ', ' : '');
  }
  
  updateTagsPreview();
  hideSuggestions();
  input.focus();
}

function getSelectedTags() {
  const input = document.getElementById('postTags');
  if (!input) return [];
  
  return input.value
    .split(',')
    .map(t => t.trim().replace(/^#/, ''))
    .filter(t => t.length > 0);
}

function parseTags(input) {
  return input
    .split(',')
    .map(t => t.trim().replace(/^#/, '').toLowerCase())
    .filter(t => t.length > 0);
}

function updateTagsPreview() {
  const preview = document.getElementById('tagsPreview');
  const tags = getSelectedTags();
  
  if (tags.length === 0) {
    preview.innerHTML = '';
    return;
  }
  
  preview.innerHTML = tags.map(tag => `<span class="tag-preview">#${tag}</span>`).join('');
}

export function isCreatePostModalOpen() {
  return isOpen;
}
