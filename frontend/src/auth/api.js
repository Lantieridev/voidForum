const API_BASE_URL = 'http://localhost:8080/api';

class ApiError extends Error {
  constructor(message, status) {
    super(message);
    this.status = status;
  }
}

async function request(endpoint, options = {}) {
  const token = localStorage.getItem('token');

  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  };

  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }

  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
    const text = await response.text();

    let data;
    try {
      data = text ? JSON.parse(text) : {};
    } catch {
      data = { error: text };
    }

    if (!response.ok) {
      throw new ApiError(data.error || `Error HTTP ${response.status}: ${text}`, response.status);
    }

    return data;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError('Error de conexión con el servidor: ' + error.message, 0);
  }
}

export const authApi = {
  login: async (username, password) => {
    return request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    });
  },

  register: async (username, email, password) => {
    return request('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, email, password }),
    });
  },

  getCurrentUser: async () => {
    return request('/auth/me');
  },

  getCurrentUserProfile: async () => {
    return request('/users/me');
  },

  getUserById: async (id) => {
    return request(`/users/${id}`);
  },
};

export const postsApi = {
  getAll: async () => {
    return request('/posts');
  },

  create: async (content, tags = []) => {
    return request('/posts', {
      method: 'POST',
      body: JSON.stringify({ content, tags }),
    });
  },

  update: async (id, content, tags) => {
    return request(`/posts/${id}`, {
      method: 'PUT',
      body: JSON.stringify({ content, tags }),
    });
  },

  delete: async (id) => {
    return request(`/posts/${id}`, {
      method: 'DELETE',
    });
  },
};

export const commentsApi = {
  getByPost: async (postId) => {
    return request(`/comments/post/${postId}`);
  },

  create: async (postId, content, parentCommentId = null) => {
    return request('/comments', {
      method: 'POST',
      body: JSON.stringify({ postId, content, parentCommentId }),
    });
  },

  update: async (id, content) => {
    return request(`/comments/${id}`, {
      method: 'PUT',
      body: JSON.stringify({ content }),
    });
  },

  delete: async (id) => {
    return request(`/comments/${id}`, {
      method: 'DELETE',
    });
  },
};

export const votesApi = {
  vote: async (targetId, value, targetType = 'post') => {
    return request(`/votes/${targetId}?value=${value}&targetType=${targetType}`, {
      method: 'POST',
    });
  },

  getUserVotedPosts: async () => {
    return request('/votes/user');
  },

  getPostVoteCount: async (targetId) => {
    return request(`/votes/${targetId}/count?targetType=post`);
  },

  getCommentVoteCount: async (targetId) => {
    return request(`/votes/${targetId}/count?targetType=comment`);
  },
};

export const usersApi = {
  updateProfile: async (data) => {
    return request('/users/me', {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  },

  changePassword: async (currentPassword, newPassword) => {
    return request('/users/me/password', {
      method: 'PUT',
      body: JSON.stringify({ currentPassword, newPassword }),
    });
  },

  updateNotifications: async (prefs) => {
    return request('/users/me/notifications', {
      method: 'PUT',
      body: JSON.stringify(prefs),
    });
  },

  deleteAccount: async (password) => {
    return request('/users/me', {
      method: 'DELETE',
      body: JSON.stringify({ password }),
    });
  },
};

export { ApiError };
