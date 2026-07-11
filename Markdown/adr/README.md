# Architecture Decision Records

Decisiones de arquitectura de voidForum. Cada ADR documenta el contexto, la decisión y las consecuencias — para que quede razonamiento escrito, no solo código.

| # | Título | Estado |
|---|---|---|
| [0001](./0001-mongodb-document-store.md) | MongoDB como document store para hilos de discusión | Aceptada |
| [0002](./0002-stateless-jwt-auth-filter.md) | Autenticación stateless con filtro JWT propio | Aceptada |
| [0003](./0003-secrets-via-environment-variables.md) | Secretos (Mongo URI, JWT secret) solo por variable de entorno | Aceptada |

## Cuándo agregar un ADR nuevo

Cuando se sume una feature que cambie una decisión existente (ej. pasar de JWT stateless a sesiones, o agregar un segundo datastore), o cuando se tome una decisión no obvia leyendo el código. No hace falta un ADR por cada PR.
