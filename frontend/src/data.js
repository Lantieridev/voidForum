export const currentUser = {
  id: 'user-1',
  username: 'ornella_dev',
  displayName: 'Ornella',
  avatar: null
};

export const posts = [
  {
    id: 'post-1',
    user: {
      id: 'user-2',
      username: 'juan_perez',
      displayName: 'Juan Pérez',
      avatar: null,
      verified: true
    },
    content: 'Acabo de terminar mi primer proyecto con Spring Boot y MongoDB. Les comparto algunos aprendizajes que me habrían ahorrado muchas horas de debugging. 🚀',
    tags: ['springboot', 'mongodb', 'java'],
    createdAt: new Date(Date.now() - 2 * 60 * 60 * 1000),
    votes: 42,
    comments: [
      {
        id: 'comment-1',
        user: {
          id: 'user-3',
          username: 'maria_dev',
          displayName: 'María García',
          avatar: null
        },
        content: 'Muy buen post! Yo también estoy aprendiendo Spring Boot, qué recursos usaste?',
        createdAt: new Date(Date.now() - 1 * 60 * 60 * 1000)
      },
      {
        id: 'comment-2',
        user: {
          id: 'user-4',
          username: 'carlos_code',
          displayName: 'Carlos Rodríguez',
          avatar: null
        },
        content: 'Gracias por compartir! Podrías hacer un tutorial más detallado?',
        createdAt: new Date(Date.now() - 30 * 60 * 1000)
      }
    ]
  },
  {
    id: 'post-2',
    user: {
      id: 'user-3',
      username: 'maria_dev',
      displayName: 'María García',
      avatar: null,
      verified: false
    },
    content: 'Cuál es la mejor forma de manejar autenticación en una API REST? JWT o sesiones tradicionales?',
    tags: ['backend', 'auth', 'api'],
    createdAt: new Date(Date.now() - 5 * 60 * 60 * 1000),
    votes: 15,
    comments: [
      {
        id: 'comment-3',
        user: {
          id: 'user-5',
          username: 'tech_guru',
          displayName: 'Tech Guru',
          avatar: null,
          verified: true
        },
        content: 'JWT es más moderno y escalable para APIs. Pero tené cuidado con el almacenamiento del token!',
        createdAt: new Date(Date.now() - 4 * 60 * 60 * 1000)
      }
    ]
  },
  {
    id: 'post-3',
    user: {
      id: 'user-5',
      username: 'tech_guru',
      displayName: 'Tech Guru',
      avatar: null,
      verified: true
    },
    content: 'Nuevo artículo: Las 10 mejores prácticas para escribir código limpio en JavaScript. Link en los comentarios! 📝',
    tags: ['javascript', 'cleancode', 'tips'],
    createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000),
    votes: 128,
    comments: []
  },
  {
    id: 'post-4',
    user: {
      id: 'user-6',
      username: 'python_ninja',
      displayName: 'Python Ninja',
      avatar: null,
      verified: false
    },
    content: 'Alguien sabe cómo optimizar consultas en MongoDB? Tengo una colección con más de 1M de documentos y las queries están tardando demasiado.',
    tags: ['mongodb', 'database', 'performance'],
    createdAt: new Date(Date.now() - 48 * 60 * 60 * 1000),
    votes: 23,
    comments: [
      {
        id: 'comment-4',
        user: {
          id: 'user-2',
          username: 'juan_perez',
          displayName: 'Juan Pérez',
          avatar: null
        },
        content: 'Asegurate de tener índices en los campos que usas en tus queries. Puedes usar explain() para ver el plan de ejecución.',
        createdAt: new Date(Date.now() - 47 * 60 * 60 * 1000)
      },
      {
        id: 'comment-5',
        user: {
          id: 'user-6',
          username: 'python_ninja',
          displayName: 'Python Ninja',
          avatar: null
        },
        content: 'Gracias! Voy a probar eso ahora mismo.',
        createdAt: new Date(Date.now() - 46 * 60 * 60 * 1000)
      }
    ]
  }
];

export function formatTimeAgo(date) {
  const seconds = Math.floor((new Date() - date) / 1000);
  
  if (seconds < 60) return 'ahora';
  
  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return `${minutes}m`;
  
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}h`;
  
  const days = Math.floor(hours / 24);
  if (days < 7) return `${days}d`;
  
  return date.toLocaleDateString('es-ES', { day: 'numeric', month: 'short' });
}

export function getInitials(name) {
  return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
}