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
    const data = await response.json();

    if (!response.ok) {
      throw new ApiError(data.error || 'Error en la solicitud', response.status);
    }

    return data;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError('Error de conexión con el servidor', 0);
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

  create: async (postId, content) => {
    return request('/comments', {
      method: 'POST',
      body: JSON.stringify({ postId, content }),
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
  vote: async (targetId, value) => {
    return request(`/votes/${targetId}?value=${value}`, {
      method: 'POST',
    });
  },
};

export { ApiError };
