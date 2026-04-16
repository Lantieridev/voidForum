import { posts, formatTimeAgo, getInitials } from './data.js';
import { init as initAuth, onAuthChange, logout as authLogout, isAuthenticated, getUser } from './auth/authManager.js';
import { openLoginModal } from './auth/LoginModal.js';
import { openRegisterModal } from './auth/RegisterModal.js';
import { showRequireAuthCard } from './auth/requireAuth.js';

const API_BASE_URL = 'http://localhost:8080/api';
import { authApi, votesApi, postsApi } from './auth/api.js';
import { openCreatePostModal } from './posts/CreatePostModal.js';
import { openEditPostModal } from './posts/EditPostModal.js';
import { openSettingsModal } from './settings/SettingsModal.js';

const icons = {
  logo: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/></svg>`,
  chevronDown: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m6 9 6 6 6-6"/></svg>`,
  search: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>`,
  plus: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>`,
  arrowLeft: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m12 19-7-7 7-7"/><path d="M19 12H5"/></svg>`,
  moon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>`,
  sun: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>`,
  logout: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>`,
  settings: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>`,
  user: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`,
  message: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>`,
  share: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/><line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/></svg>`,
  like: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg>`,
  dislike: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10 15v4a3 3 0 0 0 3 3l4-9V2H5.72a2 2 0 0 0-2 1.7l-1.38 9a2 2 0 0 0 2 2.3zm7-13h2.67A2.31 2.31 0 0 1 22 4v7a2.31 2.31 0 0 1-2.33 2H17"/></svg>`,
  verified: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M22.5 12.5c0-1.58-.875-2.95-2.148-3.6.154-.435.238-.905.238-1.4 0-2.21-1.71-3.998-3.818-3.998-.47 0-.92.084-1.336.25C14.818 2.415 13.51 1.5 12 1.5s-2.816.917-3.437 2.25c-.415-.165-.866-.25-1.336-.25-2.11 0-3.818 1.79-3.818 4 0 .494.083.964.237 1.4-1.272.65-2.147 2.018-2.147 3.6 0 1.495.782 2.798 1.942 3.486-.02.17-.032.34-.032.514 0 2.21 1.708 4 3.818 4 .47 0 .92-.086 1.335-.25.62 1.334 1.926 2.25 3.437 2.25 1.512 0 2.818-.916 3.437-2.25.415.163.865.248 1.336.248 2.11 0 3.818-1.79 3.818-4 0-.174-.012-.344-.033-.513 1.158-.687 1.943-1.99 1.943-3.484zm-6.616-3.334l-4.334 6.5c-.145.217-.382.334-.625.334-.143 0-.288-.04-.416-.126l-.115-.094-2.415-2.415c-.293-.293-.293-.768 0-1.06s.768-.294 1.06 0l1.77 1.767 3.825-5.74c.23-.345.696-.436 1.04-.207.346.23.44.696.21 1.04z"/></svg>`,
  heart: `<svg viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>`,
  file: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>`,
  bookmark: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m19 21-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>`,
  bookmarkFilled: `<svg viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2"><path d="m19 21-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>`,
  edit: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>`,
  trash: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>`
};

let userVotes = {};
let currentView = 'feed';
let userVotedPosts = [];
let userPostCount = 0;

const POPULAR_TAGS = ['java', 'javascript', 'python', 'spring', 'react', 'angular', 'vue', 'nodejs', 'mongodb', 'mysql', 'docker', 'kubernetes', 'git', 'api', 'backend', 'frontend', 'fullstack', 'devops', 'cloud', 'aws'];

function createNavbar() {
  const isDark = document.body.classList.contains('dark-theme');
  const loggedIn = window.isLoggedIn;
  const user = window.currentUser;

  const userSection = loggedIn && user ? `
    <div class="user-menu">
      <button class="user-avatar-btn" id="userMenuBtn" title="${user.displayName || user.username}">
        <div class="user-avatar">${getInitials(user.displayName || user.username)}</div>
        <span class="user-menu-chevron">${icons.chevronDown}</span>
      </button>
      <div class="dropdown-menu" id="userDropdown">
        <button class="dropdown-item" id="viewProfileBtn" onclick="window.navigateTo('profile')">
          ${icons.user}
          <span>Mi perfil</span>
        </button>
        <div class="dropdown-divider"></div>
        <button class="dropdown-item" id="settingsBtn">
          ${icons.settings}
          <span>Configuración</span>
        </button>
        <button class="dropdown-item" id="logoutBtn">
          ${icons.logout}
          <span>Cerrar sesión</span>
        </button>
      </div>
    </div>
  ` : `
    <button class="login-btn" id="loginBtn">
      Iniciar Sesión
    </button>
  `;

  return `
    <nav class="navbar">
      <div class="navbar-logo" onclick="window.navigateTo('feed')">
        ${icons.logo}
        <span>VoidForum</span>
      </div>
      <div class="navbar-actions">
        <button class="theme-toggle ${isDark ? 'dark' : ''}" id="themeToggleBtn" title="Cambiar tema">
          <span class="theme-toggle-slider">
            <span class="theme-toggle-icon">
              ${isDark ? icons.moon : icons.sun}
            </span>
          </span>
        </button>
        ${userSection}
      </div>
    </nav>
  `;
}

function createSearchBar() {
  const searchValue = window.isSearching ? window.lastSearchQuery || '' : '';
  return `
    <div class="search-container">
      <div class="search-box">
        <span class="search-icon">${icons.search}</span>
        <input type="text" id="searchInput" class="search-input" placeholder="Buscar posts, usuarios, tags..." value="${searchValue}" />
      </div>
    </div>
  `;
}

let searchTimeout;

function initSearch() {
  const searchInput = document.getElementById('searchInput');
  if (!searchInput) return;

  searchInput.addEventListener('keydown', async (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      const query = searchInput.value.trim();
      
      if (query.length === 0) {
        await loadPosts();
        window.isSearching = false;
        window.searchResults = [];
        render();
      } else {
        await searchPosts(query);
      }
    }
  });

  searchInput.addEventListener('input', () => {
    const query = searchInput.value.trim();
    if (query.length === 0) {
      window.isSearching = false;
      window.searchResults = [];
    }
  });
}

async function searchPosts(query) {
  try {
    let endpoint = '';
    let searchType = '';

    if (query.startsWith('#')) {
      const tag = query.slice(1).trim();
      if (tag) {
        endpoint = `${API_BASE_URL}/posts/search/by-tag?tag=${encodeURIComponent(tag)}`;
        searchType = `hashtag #${tag}`;
      }
    } else if (query.startsWith('@')) {
      const username = query.slice(1).trim();
      if (username) {
        endpoint = `${API_BASE_URL}/posts/search/by-author?username=${encodeURIComponent(username)}`;
        searchType = `usuario @${username}`;
      }
    } else {
      endpoint = `${API_BASE_URL}/posts/search/by-content?content=${encodeURIComponent(query)}`;
      searchType = `contenido "${query}"`;
    }

    if (endpoint) {
      const response = await fetch(endpoint);
      if (response.ok) {
        const posts = await response.json();
        window.searchResults = posts;
        window.isSearching = true;
        window.lastSearchQuery = query;
        window.lastSearchType = searchType;
        render();
      }
    }
  } catch (error) {
    console.error('Error searching posts:', error);
  }
}

function createPostCard(post, showActions = true) {
  const voteState = userVotes[post.id] || 0;
  const currentUser = window.currentUser;
  const isAuthor = currentUser && (currentUser.id === post.authorId || currentUser.username === post.authorUsername);

  const authorName = post.authorUsername || 'Usuario';
  const authorUsername = post.authorUsername || 'usuario';

  return `
    <article class="post-card ${isAuthor ? 'is-author' : ''}" data-post-id="${post.id}" onclick="window.openPost('${post.id}')">
      <div class="post-header" onclick="event.stopPropagation()">
        <div class="post-avatar">${getInitials(authorName)}</div>
        <div class="post-user-info">
          <div class="post-username">
            ${authorName}
          </div>
          <div class="post-time">@${authorUsername} · ${formatTimeAgo(post.createdAt)}</div>
        </div>
        ${isAuthor && showActions ? `
          <div class="post-owner-actions">
            <button class="owner-action-btn edit-btn" data-post-id="${post.id}" onclick="event.stopPropagation(); window.handleEditPost('${post.id}')" title="Editar">
              ${icons.edit}
            </button>
            <button class="owner-action-btn delete-btn" data-post-id="${post.id}" onclick="event.stopPropagation(); window.handleDeletePost('${post.id}')" title="Eliminar">
              ${icons.trash}
            </button>
          </div>
        ` : ''}
      </div>
      <div class="post-content">${post.content}</div>
      ${post.tags?.length > 0 ? `
        <div class="post-tags">
          ${post.tags.map(tag => `<span class="post-tag tag-${tag}">#${tag}</span>`).join('')}
        </div>
      ` : ''}
      ${showActions ? `
        <div class="post-actions">
          <button class="action-btn vote-up ${voteState === 1 ? 'active' : ''}" data-post-id="${post.id}" data-vote="1" onclick="event.stopPropagation(); window.handleVote('${post.id}', 1)">
            ${icons.like}
            <span>${post.voteCount || 0}</span>
          </button>
          <button class="action-btn vote-down ${voteState === -1 ? 'active' : ''}" data-post-id="${post.id}" data-vote="-1" onclick="event.stopPropagation(); window.handleVote('${post.id}', -1)">
            ${icons.dislike}
          </button>
          <button class="action-btn comment-btn" data-post-id="${post.id}" onclick="event.stopPropagation(); window.handleComment('${post.id}')">
            ${icons.message}
            <span>0</span>
          </button>
          <button class="action-btn" onclick="event.stopPropagation()">
            ${icons.share}
            <span>Compartir</span>
          </button>
        </div>
      ` : ''}
    </article>
  `;
}

function createFeed() {
  const displayPosts = window.isSearching ? (window.searchResults || []) : posts;
  const searchLabel = window.isSearching ? 
    `<div class="search-results-label"><span class="label-text">Resultado de búsqueda:</span><span class="search-term">${window.lastSearchQuery || ''}</span></div>` : '';
  
  return `
    <main class="content-wrapper">
      <section class="glass-plate composer">
        <div class="plate-content">
          <div class="input-row">
            <span class="prompt">>>></span>
            <textarea 
              id="composerInput"
              placeholder="Iniciar transmisión..." 
              rows="1"
              oninput="autoGrow(this)"
            ></textarea>
          </div>
          <div class="composer-tags-row">
            <input 
              type="text" 
              id="composerTagsInput" 
              class="composer-tags-input" 
              placeholder="#tags"
              autocomplete="off"
            />
            <div class="tags-suggestions composer-tags-suggestions" id="composerTagsSuggestions"></div>
          </div>
          <div class="tags-preview composer-tags-preview" id="composerTagsPreview"></div>
          <div class="composer-actions">
            <button class="btn-void" id="composerSubmitBtn" onclick="handleComposerSubmit()">TRANSMITIR</button>
          </div>
        </div>
      </section>
      ${searchLabel}
      <div class="stream">
        ${displayPosts.length > 0 ? displayPosts.map(post => createPostCard(post)).join('') : '<p class="no-posts">No hay posts aún.</p>'}
      </div>
    </main>
  `;
}

function clearSearch() {
  window.isSearching = false;
  window.searchResults = [];
  window.lastSearchQuery = '';
  window.lastSearchType = '';
  const searchInput = document.getElementById('searchInput');
  if (searchInput) searchInput.value = '';
  loadPosts().then(() => render());
}

function autoGrow(element) {
  element.style.height = '';
  element.style.height = element.scrollHeight + 'px';
}

let composerTags = [];

function getComposerTags() {
  const input = document.getElementById('composerTagsInput');
  if (!input) return [];
  return input.value
    .split(' ')
    .map(t => t.trim().replace(/^#/, ''))
    .filter(t => t.length > 0);
}

function updateComposerTagsPreview() {
  const preview = document.getElementById('composerTagsPreview');
  if (!preview) return;
  
  const tags = getComposerTags();
  if (tags.length === 0) {
    preview.innerHTML = '';
    return;
  }
  
  preview.innerHTML = tags.map(tag => `<span class="tag-preview">#${tag}</span>`).join('');
}

function handleComposerTagInput(e) {
  const input = e.target;
  const value = input.value;
  
  const parts = value.split(' ');
  const lastPart = parts[parts.length - 1];
  
  if (lastPart.startsWith('#')) {
    const search = lastPart.slice(1).toLowerCase();
    const suggestions = POPULAR_TAGS.filter(tag => 
      tag.toLowerCase().includes(search) && 
      !getComposerTags().includes(tag)
    );
    showComposerSuggestions(suggestions);
  } else if (lastPart === '' && parts.length > 1) {
    const previousPart = parts[parts.length - 2];
    if (previousPart.startsWith('#') && previousPart.length > 1) {
      const tagToAdd = previousPart.slice(1).trim();
      if (tagToAdd && !getComposerTags().includes(tagToAdd)) {
        const tags = getComposerTags();
        tags.push(tagToAdd);
        input.value = tags.map(t => `#${t}`).join(' ') + ' ';
        updateComposerTagsPreview();
        hideComposerSuggestions();
      }
    }
  } else {
    hideComposerSuggestions();
  }
  
  updateComposerTagsPreview();
}

function showComposerSuggestions(suggestions) {
  const container = document.getElementById('composerTagsSuggestions');
  if (!container || suggestions.length === 0) {
    hideComposerSuggestions();
    return;
  }

  container.innerHTML = suggestions.map((tag, index) => `
    <div class="tag-suggestion ${index === 0 ? 'selected' : ''}" data-tag="${tag}">
      #${tag}
    </div>
  `).join('');

  container.querySelectorAll('.tag-suggestion').forEach(el => {
    el.addEventListener('click', () => {
      addComposerTag(el.dataset.tag);
    });
  });

  container.style.display = 'block';
}

function hideComposerSuggestions() {
  const container = document.getElementById('composerTagsSuggestions');
  if (container) {
    container.style.display = 'none';
  }
}

function addComposerTag(tag) {
  const input = document.getElementById('composerTagsInput');
  const tags = getComposerTags();
  
  if (!tags.includes(tag)) {
    tags.push(tag);
    input.value = tags.map(t => `#${t}`).join(' ') + ' ';
  }
  
  updateComposerTagsPreview();
  hideComposerSuggestions();
  input.focus();
}

function handleComposerTagKeydown(e) {
  if (e.key === 'Enter') {
    e.preventDefault();
    const suggestions = document.querySelector('.tag-suggestion.selected');
    if (suggestions) {
      addComposerTag(suggestions.dataset.tag);
    }
  }
}

async function handleComposerSubmit() {
  const input = document.getElementById('composerInput');
  const content = input.value.trim();
  const tags = getComposerTags();
  const submitBtn = document.getElementById('composerSubmitBtn');
  
  if (!content) return;
  
  if (!isAuthenticated()) {
    showRequireAuthCard('crear un post');
    return;
  }
  
  submitBtn.disabled = true;
  submitBtn.textContent = 'TRANSMITIENDO...';
  
  try {
    console.log('Creando post con:', { content, tags });
    const result = await postsApi.create(content, tags);
    console.log('Post creado:', result);
    
    input.value = '';
    const tagsInput = document.getElementById('composerTagsInput');
    tagsInput.value = '';
    composerTags = [];
    updateComposerTagsPreview();
    
    if (typeof window.refreshPosts === 'function') {
      await window.refreshPosts();
    }
  } catch (error) {
    console.error('Error al crear post:', error);
    alert('Error al crear el post: ' + (error.message || 'Error desconocido'));
  } finally {
    submitBtn.disabled = false;
    submitBtn.textContent = 'TRANSMITIR';
  }
}

function createProfileSidebar() {
  const user = window.currentUser;
  if (!user) return '';

  return `
    <aside class="profile-sidebar">
      <div class="profile-header">
        <div class="profile-avatar">${getInitials(user.displayName || user.username)}</div>
        <h2 class="profile-name">${user.displayName || user.username}</h2>
        <p class="profile-username">@${user.username}</p>
      </div>
      <div class="profile-stats">
        <div class="stat-item">
          <span class="stat-value">${userPostCount}</span>
          <span class="stat-label">Posts</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">${userVotedPosts.length}</span>
          <span class="stat-label">Votos</span>
        </div>
      </div>
      <div class="profile-section">
        <h3 class="section-title">
          ${icons.heart}
          Posts que te gustaron
        </h3>
        <div class="voted-posts">
          ${userVotedPosts.length > 0 ? userVotedPosts.map(post => `
            <div class="voted-post-card" onclick="window.openPost('${post.id}')">
              <div class="voted-post-content">${post.content.substring(0, 80)}${post.content.length > 80 ? '...' : ''}</div>
              <div class="voted-post-meta">
                <span>@${post.user?.username || 'usuario'}</span>
                <span class="vote-count">${post.votes || 0} votos</span>
              </div>
            </div>
          `).join('') : '<p class="no-votes">Aún no diste like a ningún post.</p>'}
        </div>
      </div>
    </aside>
  `;
}

function createFAB() {
  return `
    <button class="create-post-btn fab-hidden" id="createPostBtn" title="Crear nuevo post">
      ${icons.plus}
    </button>
  `;
}

let fabVisible = true;
let lastScrollY = 0;
let searchVisible = true;
const SCROLL_THRESHOLD = 50;

function handleScroll() {
  const fab = document.getElementById('createPostBtn');
  const searchContainer = document.querySelector('.search-container');
  
  const currentScrollY = window.scrollY;
  const scrollDelta = currentScrollY - lastScrollY;
  
  // Handle FAB visibility
  if (fab) {
    if (currentScrollY < 100) {
      fab.classList.add('fab-hidden');
      fabVisible = false;
    } else {
      fab.classList.remove('fab-hidden');
      fabVisible = true;
    }
  }
  
  // Handle search bar visibility based on scroll direction
  if (searchContainer) {
    if (scrollDelta > SCROLL_THRESHOLD && searchVisible) {
      // Scrolling down - hide search bar
      searchContainer.classList.add('search-hidden');
      searchVisible = false;
    } else if (scrollDelta < -SCROLL_THRESHOLD && !searchVisible) {
      // Scrolling up - show search bar
      searchContainer.classList.remove('search-hidden');
      searchVisible = true;
    }
  }
  
  lastScrollY = currentScrollY;
}

function attachScrollListener() {
  window.addEventListener('scroll', handleScroll, { passive: true });
  handleScroll();
}

let currentProfileTab = 'posts';
let userPosts = [];
let savedPosts = [];

function createProfilePage() {
  const user = window.currentUser;
  
  return `
    <div class="profile-page">
      <div class="profile-header">
        <div class="profile-avatar-large">${getInitials(user.displayName || user.username)}</div>
        <div class="profile-info">
          <h1 class="profile-display-name">${user.displayName || user.username}</h1>
          <p class="profile-username">@${user.username}</p>
        </div>
      </div>
      <nav class="profile-tabs">
        <button class="profile-tab ${currentProfileTab === 'posts' ? 'active' : ''}" data-tab="posts" id="tabPosts">
          ${icons.file}
          <span>Posts</span>
        </button>
        <button class="profile-tab ${currentProfileTab === 'likes' ? 'active' : ''}" data-tab="likes" id="tabLikes">
          ${icons.heart}
          <span>Likes</span>
        </button>
        <button class="profile-tab ${currentProfileTab === 'saved' ? 'active' : ''}" data-tab="saved" id="tabSaved">
          ${icons.bookmark}
          <span>Guardados</span>
        </button>
      </nav>
      <div class="profile-content" id="profileContent">
        ${getProfileTabContent()}
      </div>
    </div>
  `;
}

function getProfileTabContent() {
  switch (currentProfileTab) {
    case 'posts':
      return renderProfilePosts();
    case 'likes':
      return renderProfileLikes();
    case 'saved':
      return renderProfileSaved();
    default:
      return renderProfilePosts();
  }
}

function renderProfilePosts() {
  if (userPosts.length === 0) {
    return '<p class="empty-tab-message">No tienes posts todavía.</p>';
  }
  return userPosts.map(post => createPostCard(post)).join('');
}

function renderProfileLikes() {
  if (userVotedPosts.length === 0) {
    return '<p class="empty-tab-message">No tienes posts con like todavía.</p>';
  }
  return userVotedPosts.map(post => createPostCard(post)).join('');
}

function renderProfileSaved() {
  if (savedPosts.length === 0) {
    return '<p class="empty-tab-message">No tienes posts guardados todavía.</p>';
  }
  return savedPosts.map(post => createPostCard(post)).join('');
}

function renderProfile() {
  const user = window.currentUser;
  if (!user) {
    showRequireAuthCard('ver tu perfil');
    return;
  }

  currentView = 'profile';
  currentProfileTab = 'posts';

  const app = document.getElementById('app');
  app.innerHTML = createNavbar() + `
    <div class="profile-container">
      ${createProfilePage()}
    </div>
  ` + createFAB();

  attachProfileEvents();
  attachScrollListener();
  loadProfileData();
}

async function loadProfileData() {
  try {
    const response = await votesApi.getUserVotedPosts();
    userVotedPosts = response.posts || [];
    userPostCount = response.postCount || 0;
    userPosts = response.userPosts || [];
    savedPosts = response.savedPosts || [];
    updateProfileContent();
  } catch (error) {
    console.error('Error loading profile data:', error);
  }
}

function updateProfileContent() {
  const content = document.getElementById('profileContent');
  if (content) {
    content.innerHTML = getProfileTabContent();
    attachTabEvents();
  }
}

function attachProfileEvents() {
  attachCommonEvents();
  attachTabEvents();
}

function attachTabEvents() {
  const tabs = document.querySelectorAll('.profile-tab');
  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      currentProfileTab = tab.dataset.tab;
      tabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      updateProfileContent();
    });
  });
}

window.handleVote = async (postId, vote) => {
  if (!isAuthenticated()) {
    showRequireAuthCard('dar like a un post');
    return;
  }

  const currentVote = userVotes[postId] || 0;

  if (currentVote === vote) {
    userVotes[postId] = 0;
  } else {
    userVotes[postId] = vote;
  }

  updateVoteUI(postId);

  try {
    const { votes } = await votesApi.vote(postId, userVotes[postId]);
    const voteUpBtn = document.querySelector(`[data-post-id="${postId}"].vote-up`);
    if (voteUpBtn) {
      voteUpBtn.querySelector('span').textContent = votes;
    }
    const post = posts.find(p => p.id === postId);
    if (post) {
      post.voteCount = votes;
    }

    const likedIndex = userVotedPosts.findIndex(p => p.id === postId);

    if (userVotes[postId] === 1 && likedIndex === -1) {
      userVotedPosts.push({...post});
    } else if (userVotes[postId] === 0 && likedIndex !== -1) {
      userVotedPosts.splice(likedIndex, 1);
    }

    if (currentView === 'profile' && currentProfileTab === 'likes') {
      updateProfileContent();
    } else if (currentView === 'feed') {
      updateVoteUI(postId);
    }
  } catch (error) {
    console.error('Error voting:', error);
    userVotes[postId] = currentVote;
    updateVoteUI(postId);
  }
};

function updateVoteUI(postId) {
  document.querySelectorAll(`[data-post-id="${postId}"].vote-up, [data-post-id="${postId}"].vote-down`).forEach(b => {
    b.classList.remove('active');
  });

  if (userVotes[postId] !== 0) {
    const activeBtn = document.querySelector(`[data-post-id="${postId}"][data-vote="${userVotes[postId]}"]`);
    activeBtn?.classList.add('active');
  }
}

window.handleComment = (postId) => {
  if (!isAuthenticated()) {
    showRequireAuthCard('comentar');
    return;
  }
  console.log('Comment action for post:', postId);
};

window.handleCreatePost = () => {
  if (!isAuthenticated()) {
    showRequireAuthCard('crear un post');
    return;
  }
  openCreatePostModal();
};

window.handleEditPost = (postId) => {
  const post = posts.find(p => p.id === postId);
  if (!post) return;
  
  if (!isAuthenticated()) {
    showRequireAuthCard('editar un post');
    return;
  }
  
  openEditPostModal(post);
};

window.handleDeletePost = async (postId) => {
  if (!isAuthenticated()) {
    showRequireAuthCard('eliminar un post');
    return;
  }
  
  const confirmed = confirm('¿Estás seguro de que querés eliminar este post?');
  if (!confirmed) return;
  
  try {
    await postsApi.delete(postId);
    posts.splice(posts.findIndex(p => p.id === postId), 1);
    
    if (typeof window.refreshPosts === 'function') {
      window.refreshPosts();
    }
  } catch (error) {
    console.error('Error deleting post:', error);
    alert('Error al eliminar el post: ' + (error.message || 'Error desconocido'));
  }
};

window.navigateTo = (view) => {
  if (view === 'profile') {
    window.history.pushState({ view: 'profile' }, '', '#/profile');
    renderProfile();
  } else {
    window.history.pushState({ view: 'feed' }, '', window.location.pathname);
    render();
  }
};

function renderPostDetail(postId) {
  const post = posts.find(p => p.id === postId);
  if (!post) return;

  currentView = 'detail';
  const voteState = userVotes[post.id] || 0;

  const postDetail = `
    <div class="post-detail-container">
      <button class="back-btn" id="backBtn">
        ${icons.arrowLeft}
        <span>Volver</span>
      </button>
      <article class="post-card post-full">
        <div class="post-header">
          <div class="post-avatar">${getInitials(post.user?.displayName || post.user?.username || 'U')}</div>
          <div class="post-user-info">
            <div class="post-username">
              ${post.user?.displayName || post.user?.username || 'Usuario'}
              ${post.user?.verified ? `<span class="verified-badge">${icons.verified}</span>` : ''}
            </div>
            <div class="post-time">@${post.user?.username || 'usuario'} · ${formatTimeAgo(post.createdAt)}</div>
          </div>
        </div>
        <div class="post-content">${post.content}</div>
        ${post.tags?.length > 0 ? `
          <div class="post-tags">
            ${post.tags.map(tag => `<span class="post-tag tag-${tag}">#${tag}</span>`).join('')}
          </div>
        ` : ''}
        <div class="post-actions">
          <button class="action-btn vote-up ${voteState === 1 ? 'active' : ''}" data-post-id="${post.id}" data-vote="1" onclick="window.handleVote('${post.id}', 1)">
            ${icons.like}
            <span>${(post.votes || 0) + voteState}</span>
          </button>
          <button class="action-btn vote-down ${voteState === -1 ? 'active' : ''}" data-post-id="${post.id}" data-vote="-1" onclick="window.handleVote('${post.id}', -1)">
            ${icons.dislike}
          </button>
        </div>
      </article>
      <div class="comments-container">
        <h3 class="comments-title">Comentarios (${post.comments?.length || 0})</h3>
        ${post.comments?.length > 0 ? post.comments.map(comment => `
          <div class="comment">
            <div class="comment-avatar">${getInitials(comment.user?.displayName || comment.user?.username || 'U')}</div>
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-username">${comment.user?.displayName || comment.user?.username || 'Usuario'}</span>
                <span class="comment-time">${formatTimeAgo(comment.createdAt)}</span>
              </div>
              <p class="comment-text">${comment.content}</p>
            </div>
          </div>
        `).join('') : '<p class="no-comments">No hay comentarios aún.</p>'}
      </div>
    </div>
  `;

  const app = document.getElementById('app');
  app.innerHTML = createNavbar() + postDetail + createFAB();
  attachPostDetailEvents();
  attachScrollListener();
}

function attachPostDetailEvents() {
  const backBtn = document.getElementById('backBtn');
  backBtn?.addEventListener('click', () => {
    window.history.back();
  });

  attachCommonEvents();
}

function attachCommonEvents() {
  const themeToggleBtn = document.getElementById('themeToggleBtn');
  const userMenuBtn = document.getElementById('userMenuBtn');
  const userDropdown = document.getElementById('userDropdown');
  const logoutBtn = document.getElementById('logoutBtn');
  const settingsBtn = document.getElementById('settingsBtn');
  const viewProfileBtn = document.getElementById('viewProfileBtn');
  const loginBtn = document.getElementById('loginBtn');
  const createPostBtn = document.getElementById('createPostBtn');

  themeToggleBtn?.addEventListener('click', () => {
    const isDark = document.body.classList.contains('dark-theme');

    if (isDark) {
      document.body.classList.remove('dark-theme');
      document.body.classList.add('light-theme');
      localStorage.setItem('theme', 'light');
      themeToggleBtn.classList.remove('dark');
      themeToggleBtn.querySelector('.theme-toggle-icon').innerHTML = icons.sun;
    } else {
      document.body.classList.remove('light-theme');
      document.body.classList.add('dark-theme');
      localStorage.setItem('theme', 'dark');
      themeToggleBtn.classList.add('dark');
      themeToggleBtn.querySelector('.theme-toggle-icon').innerHTML = icons.moon;
    }
  });

  userMenuBtn?.addEventListener('click', (e) => {
    e.stopPropagation();
    const userMenu = userMenuBtn.closest('.user-menu');
    userDropdown.classList.toggle('active');
    userMenu.classList.toggle('active');
  });

  document.addEventListener('click', (e) => {
    if (!e.target.closest('.user-menu')) {
      const userMenu = document.querySelector('.user-menu');
      userDropdown?.classList.remove('active');
      userMenu?.classList.remove('active');
    }
  });

  logoutBtn?.addEventListener('click', () => {
    authLogout();
    window.navigateTo('feed');
  });

  settingsBtn?.addEventListener('click', () => {
    const userMenu = document.querySelector('.user-menu');
    userDropdown?.classList.remove('active');
    userMenu?.classList.remove('active');
    openSettingsModal();
  });

  viewProfileBtn?.addEventListener('click', () => {
    const userMenu = document.querySelector('.user-menu');
    userDropdown?.classList.remove('active');
    userMenu?.classList.remove('active');
    window.navigateTo('profile');
  });

  loginBtn?.addEventListener('click', () => {
    openLoginModal(null, () => {
      openRegisterModal();
    });
  });

  createPostBtn?.addEventListener('click', window.handleCreatePost);

  const composerTagsInput = document.getElementById('composerTagsInput');
  composerTagsInput?.addEventListener('input', handleComposerTagInput);
  composerTagsInput?.addEventListener('keydown', handleComposerTagKeydown);

  const savedTheme = localStorage.getItem('theme');
  if (savedTheme === 'light') {
    document.body.classList.remove('dark-theme');
    document.body.classList.add('light-theme');
    if (themeToggleBtn) {
      themeToggleBtn.classList.remove('dark');
      themeToggleBtn.querySelector('.theme-toggle-icon').innerHTML = icons.sun;
    }
  } else {
    document.body.classList.remove('light-theme');
    document.body.classList.add('dark-theme');
    if (themeToggleBtn) {
      themeToggleBtn.classList.add('dark');
      themeToggleBtn.querySelector('.theme-toggle-icon').innerHTML = icons.moon;
    }
  }
}

function render() {
  currentView = 'feed';
  const app = document.getElementById('app');
  app.innerHTML = createNavbar() + createSearchBar() + createFeed() + createFAB();
  initSearch();
  attachEventListeners();
  attachScrollListener();
  
  const savedTheme = localStorage.getItem('theme');
  if (savedTheme === 'light') {
    document.body.classList.remove('dark-theme');
    document.body.classList.add('light-theme');
  } else {
    document.body.classList.remove('light-theme');
    document.body.classList.add('dark-theme');
  }
}

function attachEventListeners() {
  attachCommonEvents();
}

window.refreshUI = () => {
  if (currentView === 'profile') {
    renderProfile();
  } else {
    render();
  }
};

document.addEventListener('DOMContentLoaded', async () => {
  await initAuth();
  await loadUserVotes();
  await loadPosts();

  onAuthChange(async () => {
    await loadUserVotes();
    if (typeof window.refreshUI === 'function') {
      window.refreshUI();
    }
  });

  window.openPost = (postId) => {
    window.history.pushState({ postId }, '', `#post/${postId}`);
    renderPostDetail(postId);
  };

  window.addEventListener('popstate', (event) => {
    if (event.state?.view === 'profile') {
      renderProfile();
    } else if (event.state?.postId) {
      renderPostDetail(event.state.postId);
    } else if (window.location.hash.startsWith('#/profile')) {
      renderProfile();
    } else if (window.location.hash.startsWith('#post/')) {
      const postId = window.location.hash.replace('#post/', '');
      renderPostDetail(postId);
    } else {
      render();
    }
  });

  if (window.location.hash === '#/profile') {
    renderProfile();
  } else {
    render();
  }
});

async function loadUserVotes() {
  if (!isAuthenticated()) {
    userVotes = {};
    return;
  }

  try {
    const response = await votesApi.getUserVotedPosts();
    userVotedPosts = response.posts || [];
    userPostCount = response.postCount || 0;
    userPosts = response.userPosts || [];
    savedPosts = response.savedPosts || [];

    userVotes = {};
    userVotedPosts.forEach(post => {
      userVotes[post.id] = 1;
    });
  } catch (error) {
    console.error('Error loading user votes:', error);
  }
}

async function loadPosts() {
  try {
    const response = await postsApi.getAll();
    const postsData = Array.isArray(response) ? response : (response.posts || []);
    posts.splice(0, posts.length, ...postsData);
    render();
  } catch (error) {
    console.error('Error loading posts:', error);
  }
}

window.refreshPosts = async () => {
  try {
    const response = await postsApi.getAll();
    const postsData = Array.isArray(response) ? response : (response.posts || []);
    posts.splice(0, posts.length, ...postsData);
    
    if (currentView === 'profile') {
      renderProfile();
    } else {
      render();
    }
  } catch (error) {
    console.error('Error refreshing posts:', error);
  }
};

// Exponer funciones para uso inline en HTML
window.autoGrow = autoGrow;
window.handleComposerSubmit = handleComposerSubmit;
window.addComposerTag = addComposerTag;
