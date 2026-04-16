import { authApi, ApiError } from './api.js';

const TOKEN_KEY = 'token';
const USER_KEY = 'user';

let currentUser = null;
let isLoggedIn = false;
let listeners = [];

function notifyListeners() {
  listeners.forEach(callback => callback({ currentUser, isLoggedIn }));
}

export function onAuthChange(callback) {
  listeners.push(callback);
  return () => {
    listeners = listeners.filter(cb => cb !== callback);
  };
}

export function getUser() {
  return currentUser;
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function isAuthenticated() {
  return isLoggedIn;
}

function setUser(user) {
  currentUser = user;
  isLoggedIn = !!user;
  window.currentUser = user;
  window.isLoggedIn = isLoggedIn;

  if (user) {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  } else {
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(TOKEN_KEY);
  }

  notifyListeners();
}

export async function login(username, password) {
  try {
    const response = await authApi.login(username, password);
    localStorage.setItem('token', response.token);
    setUser(response.user);
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
}

export async function register(username, email, password) {
  try {
    await authApi.register(username, email, password);
    const loginResult = await login(username, password);
    return loginResult;
  } catch (error) {
    return { success: false, error: error.message };
  }
}

export function logout() {
  setUser(null);
}

export async function init() {
  const token = localStorage.getItem(TOKEN_KEY);
  const savedUser = localStorage.getItem(USER_KEY);

  if (savedUser) {
    try {
      const parsedUser = JSON.parse(savedUser);
      setUser(parsedUser);
    } catch {
      localStorage.removeItem(USER_KEY);
    }
  }

  if (token) {
    try {
      const response = await authApi.getCurrentUser();
      setUser(response.user);
      localStorage.setItem('token', response.token);
    } catch {
      setUser(null);
    }
  }
}

window.currentUser = currentUser;
window.isLoggedIn = isLoggedIn;
window.login = login;
window.register = register;
window.logout = logout;
window.getUser = getUser;
window.getToken = getToken;
window.isAuthenticated = isAuthenticated;
