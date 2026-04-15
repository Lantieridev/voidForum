import { currentUser, posts, formatTimeAgo, getInitials } from './data.js';

const icons = {
  logo: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/></svg>`,
  search: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>`,
  plus: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>`,
  chevronDown: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m6 9 6 6 6-6"/></svg>`,
  arrowLeft: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m12 19-7-7 7-7"/><path d="M19 12H5"/></svg>`,
  settings: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>`,
  bell: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>`,
  moon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>`,
  sun: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>`,
  logout: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>`,
  user: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`,
  message: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>`,
  share: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/><line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/></svg>`,
  like: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg>`,
  dislike: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10 15v4a3 3 0 0 0 3 3l4-9V2H5.72a2 2 0 0 0-2 1.7l-1.38 9a2 2 0 0 0 2 2.3zm7-13h2.67A2.31 2.31 0 0 1 22 4v7a2.31 2.31 0 0 1-2.33 2H17"/></svg>`,
  verified: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M22.5 12.5c0-1.58-.875-2.95-2.148-3.6.154-.435.238-.905.238-1.4 0-2.21-1.71-3.998-3.818-3.998-.47 0-.92.084-1.336.25C14.818 2.415 13.51 1.5 12 1.5s-2.816.917-3.437 2.25c-.415-.165-.866-.25-1.336-.25-2.11 0-3.818 1.79-3.818 4 0 .494.083.964.237 1.4-1.272.65-2.147 2.018-2.147 3.6 0 1.495.782 2.798 1.942 3.486-.02.17-.032.34-.032.514 0 2.21 1.708 4 3.818 4 .47 0 .92-.086 1.335-.25.62 1.334 1.926 2.25 3.437 2.25 1.512 0 2.818-.916 3.437-2.25.415.163.865.248 1.336.248 2.11 0 3.818-1.79 3.818-4 0-.174-.012-.344-.033-.513 1.158-.687 1.943-1.99 1.943-3.484zm-6.616-3.334l-4.334 6.5c-.145.217-.382.334-.625.334-.143 0-.288-.04-.416-.126l-.115-.094-2.415-2.415c-.293-.293-.293-.768 0-1.06s.768-.294 1.06 0l1.77 1.767 3.825-5.74c.23-.345.696-.436 1.04-.207.346.23.44.696.21 1.04z"/></svg>`
};

let userVotes = {};
let commentsExpanded = {};

function createNavbar() {
  const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
  
  return `
    <nav class="navbar">
      <div class="navbar-logo">
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
        <div class="user-menu">
          <button class="user-avatar-btn" id="userMenuBtn">
            <div class="user-avatar">${getInitials(currentUser.displayName)}</div>
            <span class="user-name">${currentUser.displayName}</span>
            ${icons.chevronDown}
          </button>
          <div class="dropdown-menu" id="userDropdown">
            <button class="dropdown-item">
              ${icons.user}
              <span>Perfil</span>
            </button>
            <button class="dropdown-item">
              ${icons.settings}
              <span>Configuración</span>
            </button>
            <button class="dropdown-item">
              ${icons.bell}
              <span>Notificaciones</span>
            </button>
            <div class="dropdown-divider"></div>
            <button class="dropdown-item">
              ${icons.logout}
              <span>Cerrar sesión</span>
            </button>
          </div>
        </div>
      </div>
    </nav>
  `;
}

function createSearchBar() {
  return `
    <div class="search-container">
      <div class="search-box">
        <span class="search-icon">${icons.search}</span>
        <input type="text" class="search-input" placeholder="Buscar posts, usuarios, tags..." />
      </div>
    </div>
  `;
}

function createPostCard(post) {
  const voteState = userVotes[post.id] || 0;
  
  return `
    <article class="post-card" data-post-id="${post.id}" onclick="window.openPost('${post.id}')">
      <div class="post-header" onclick="event.stopPropagation()">
        <div class="post-avatar">${getInitials(post.user.displayName)}</div>
        <div class="post-user-info">
          <div class="post-username">
            ${post.user.displayName}
            ${post.user.verified ? `<span class="verified-badge">${icons.verified}</span>` : ''}
          </div>
          <div class="post-time">@${post.user.username} · ${formatTimeAgo(post.createdAt)}</div>
        </div>
      </div>
      <div class="post-content">${post.content}</div>
      ${post.tags.length > 0 ? `
        <div class="post-tags">
          ${post.tags.map(tag => `<span class="post-tag tag-${tag}">#${tag}</span>`).join('')}
        </div>
      ` : ''}
      <div class="post-actions">
        <button class="action-btn vote-up ${voteState === 1 ? 'active' : ''}" data-post-id="${post.id}" data-vote="1" onclick="event.stopPropagation()">
          ${icons.like}
          <span>${post.votes + voteState}</span>
        </button>
        <button class="action-btn vote-down ${voteState === -1 ? 'active' : ''}" data-post-id="${post.id}" data-vote="-1" onclick="event.stopPropagation()">
          ${icons.dislike}
        </button>
        <button class="action-btn" onclick="event.stopPropagation()">
          ${icons.message}
          <span>${post.comments.length}</span>
        </button>
        <button class="action-btn" onclick="event.stopPropagation()">
          ${icons.share}
          <span>Compartir</span>
        </button>
      </div>
    </article>
  `;
}

function createFeed() {
  return `
    <div class="feed-container">
      ${posts.map(post => createPostCard(post)).join('')}
    </div>
  `;
}

function createFAB() {
  return `
    <button class="create-post-btn" title="Crear nuevo post">
      ${icons.plus}
    </button>
  `;
}

function renderPostDetail(postId) {
  const post = posts.find(p => p.id === postId);
  if (!post) return;
  
  const voteState = userVotes[post.id] || 0;
  
  const postDetail = `
    <div class="post-detail-container">
      <button class="back-btn" id="backBtn">
        ${icons.arrowLeft}
        <span>Volver</span>
      </button>
      <article class="post-card post-full">
        <div class="post-header">
          <div class="post-avatar">${getInitials(post.user.displayName)}</div>
          <div class="post-user-info">
            <div class="post-username">
              ${post.user.displayName}
              ${post.user.verified ? `<span class="verified-badge">${icons.verified}</span>` : ''}
            </div>
            <div class="post-time">@${post.user.username} · ${formatTimeAgo(post.createdAt)}</div>
          </div>
        </div>
        <div class="post-content">${post.content}</div>
        ${post.tags.length > 0 ? `
          <div class="post-tags">
            ${post.tags.map(tag => `<span class="post-tag tag-${tag}">#${tag}</span>`).join('')}
          </div>
        ` : ''}
        <div class="post-actions">
          <button class="action-btn vote-up ${voteState === 1 ? 'active' : ''}" data-post-id="${post.id}" data-vote="1">
            ${icons.like}
            <span>${post.votes + voteState}</span>
          </button>
          <button class="action-btn vote-down ${voteState === -1 ? 'active' : ''}" data-post-id="${post.id}" data-vote="-1">
            ${icons.dislike}
          </button>
        </div>
      </article>
      <div class="comments-container">
        <h3 class="comments-title">Comentarios (${post.comments.length})</h3>
        ${post.comments.length > 0 ? post.comments.map(comment => `
          <div class="comment">
            <div class="comment-avatar">${getInitials(comment.user.displayName)}</div>
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-username">${comment.user.displayName}</span>
                <span class="comment-time">${formatTimeAgo(comment.createdAt)}</span>
              </div>
              <p class="comment-text">${comment.content}</p>
            </div>
          </div>
        `).join('') : '<p class="no-comments">No hay comentarios aún. ¡Sé el primero en comentar!</p>'}
      </div>
    </div>
  `;
  
  const app = document.getElementById('app');
  app.innerHTML = createNavbar() + postDetail + createFAB();
  attachPostDetailEvents();
}

function attachPostDetailEvents() {
  const backBtn = document.getElementById('backBtn');
  backBtn?.addEventListener('click', () => {
    window.history.back();
  });
  
  const themeToggleBtn = document.getElementById('themeToggleBtn');
  themeToggleBtn?.addEventListener('click', () => {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    const isDark = newTheme === 'dark';
    
    if (isDark) {
      document.documentElement.setAttribute('data-theme', 'dark');
      localStorage.setItem('theme', 'dark');
    } else {
      document.documentElement.removeAttribute('data-theme');
      localStorage.setItem('theme', 'light');
    }
    
    if (isDark) {
      themeToggleBtn.classList.add('dark');
    } else {
      themeToggleBtn.classList.remove('dark');
    }
    themeToggleBtn.querySelector('.theme-toggle-icon').innerHTML = isDark ? icons.moon : icons.sun;
  });
  
  document.querySelectorAll('.vote-up, .vote-down').forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.stopPropagation();
      const postId = btn.dataset.postId;
      const vote = parseInt(btn.dataset.vote);
      const currentVote = userVotes[postId] || 0;
      const post = posts.find(p => p.id === postId);
      
      if (currentVote === vote) {
        userVotes[postId] = 0;
      } else {
        userVotes[postId] = vote;
      }
      
      let displayVotes = post.votes + (userVotes[postId] === 1 ? 1 : 0);
      
      const voteUpBtn = document.querySelector(`[data-post-id="${postId}"].vote-up`);
      if (voteUpBtn) {
        voteUpBtn.querySelector('span').textContent = displayVotes;
      }
      
      document.querySelectorAll(`[data-post-id="${postId}"].vote-up, [data-post-id="${postId}"].vote-down`).forEach(b => {
        b.classList.remove('active');
      });
      
      if (userVotes[postId] !== 0) {
        btn.classList.add('active');
      }
    });
  });
  
  const savedTheme = localStorage.getItem('theme');
  if (savedTheme === 'dark') {
    document.documentElement.setAttribute('data-theme', 'dark');
    if (themeToggleBtn) {
      themeToggleBtn.classList.add('dark');
      themeToggleBtn.querySelector('.theme-toggle-icon').innerHTML = icons.moon;
    }
  }
}

function render() {
  const app = document.getElementById('app');
  app.innerHTML = createNavbar() + createSearchBar() + createFeed() + createFAB();
  attachEventListeners();
}

function attachEventListeners() {
  const userMenuBtn = document.getElementById('userMenuBtn');
  const userDropdown = document.getElementById('userDropdown');
  const themeToggleBtn = document.getElementById('themeToggleBtn');

  userMenuBtn?.addEventListener('click', (e) => {
    e.stopPropagation();
    userDropdown.classList.toggle('active');
  });

  document.addEventListener('click', (e) => {
    if (!e.target.closest('.user-menu')) {
      userDropdown?.classList.remove('active');
    }
  });

  themeToggleBtn?.addEventListener('click', () => {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    const isDark = newTheme === 'dark';
    
    if (isDark) {
      document.documentElement.setAttribute('data-theme', 'dark');
      localStorage.setItem('theme', 'dark');
    } else {
      document.documentElement.removeAttribute('data-theme');
      localStorage.setItem('theme', 'light');
    }
    
    if (isDark) {
      themeToggleBtn.classList.add('dark');
    } else {
      themeToggleBtn.classList.remove('dark');
    }
    themeToggleBtn.querySelector('.theme-toggle-icon').innerHTML = isDark ? icons.moon : icons.sun;
  });

  document.querySelectorAll('.vote-up, .vote-down').forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.stopPropagation();
      const postId = btn.dataset.postId;
      const vote = parseInt(btn.dataset.vote);
      const currentVote = userVotes[postId] || 0;
      const post = posts.find(p => p.id === postId);
      
      if (currentVote === vote) {
        userVotes[postId] = 0;
      } else {
        userVotes[postId] = vote;
      }
      
      let displayVotes = post.votes + (userVotes[postId] === 1 ? 1 : 0);
      
      const voteUpBtn = document.querySelector(`[data-post-id="${postId}"].vote-up`);
      if (voteUpBtn) {
        voteUpBtn.querySelector('span').textContent = displayVotes;
      }
      
      document.querySelectorAll(`[data-post-id="${postId}"].vote-up, [data-post-id="${postId}"].vote-down`).forEach(b => {
        b.classList.remove('active');
      });
      
      if (userVotes[postId] !== 0) {
        btn.classList.add('active');
      }
    });
  });

  document.querySelectorAll('.post-link').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const postId = btn.dataset.postId;
      renderPostDetail(postId);
    });
  });

  const savedTheme = localStorage.getItem('theme');
  if (savedTheme === 'dark') {
    document.documentElement.setAttribute('data-theme', 'dark');
    if (themeToggleBtn) {
      themeToggleBtn.classList.add('dark');
      themeToggleBtn.querySelector('.theme-toggle-icon').innerHTML = icons.moon;
    }
  }
}

document.addEventListener('DOMContentLoaded', () => {
  window.openPost = (postId) => {
    window.history.pushState({ postId }, '', `#post/${postId}`);
    renderPostDetail(postId);
  };
  
  window.addEventListener('popstate', (event) => {
    if (event.state && event.state.postId) {
      renderPostDetail(event.state.postId);
    } else {
      render();
    }
  });
  
  render();
});