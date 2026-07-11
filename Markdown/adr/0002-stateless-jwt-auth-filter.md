# 0002 — Autenticación stateless con filtro JWT propio

## Estado
Aceptada

## Contexto
El backend es una API REST consumida por un frontend Vite/Tailwind separado (distintos orígenes), sin necesidad de sesiones de servidor ni cookies. Spring Security trae mecanismos de sesión por defecto que no aplican bien a este escenario API-first.

## Decisión
- `SecurityConfig` deshabilita CSRF (no aplica sin sesiones basadas en cookies) y define las reglas de autorización explícitamente: `/api/auth/**` público, `POST /api/posts/**` requiere autenticación, el resto público por defecto.
- `JwtAuthenticationFilter` (un `OncePerRequestFilter` a medida, no un `AuthenticationProvider` estándar de Spring Security) lee el header `Authorization: Bearer <token>`, valida el JWT vía `JwtService`, y si es válido carga un `UsernamePasswordAuthenticationToken` directo en el `SecurityContextHolder` — sin roles/authorities (`Collections.emptyList()`).
- El filtro se registra antes de `UsernamePasswordAuthenticationFilter` en la cadena.

## Consecuencias
- No hay manejo de roles/autorización granular todavía — cualquier usuario autenticado tiene los mismos permisos. Si se suma una feature que necesite roles (ej. moderadores), hay que poblar las `authorities` del token en vez de la lista vacía actual.
- No hay refresh tokens ni revocación — un JWT válido lo sigue siendo hasta que expira (`jwt.expiration`), no hay logout real del lado del servidor. Si se agrega esa feature, es un cambio de este ADR, no un parche silencioso.
- Al ser stateless, escalar horizontalmente el backend no requiere sticky sessions ni un store de sesión compartido.
