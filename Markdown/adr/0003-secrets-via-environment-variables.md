# 0003 — Secretos (Mongo URI, JWT secret) solo por variable de entorno

## Estado
Aceptada

## Contexto
`application.properties` tenía en algún momento una URI de MongoDB Atlas con usuario y contraseña reales hardcodeados, commiteada en este repo público. Se corrigió parametrizando a `${MONGO_URI}`, pero el string viejo sigue en el historial de git — la rotación de esa credencial en Atlas es una acción manual pendiente del dueño del repo, no algo que se arregla en código.

## Decisión
- `application.properties` no contiene ningún secreto en texto plano: `spring.data.mongodb.uri=${MONGO_URI}`, `jwt.secret=${JWT_SECRET}`, `jwt.expiration=${JWT_EXPIRATION:86400000}` (única con default, porque no es sensible).
- El README documenta explícitamente que Spring Boot lee estas variables del entorno y **no** carga un `.env` automáticamente — hay que exportarlas antes de correr `mvn spring-boot:run`.
- `.devcontainer/env.template` sirve como referencia del formato esperado, sin valores reales.

## Consecuencias
- Cualquier contribuidor nuevo necesita su propio cluster de Mongo (o uno compartido) y su propio `JWT_SECRET` — no hay manera de levantar el proyecto "out of the box" sin configurar nada, a propósito.
- Si se agrega una integración externa nueva que necesite una key (ej. un servicio de notificaciones), va acá: variable de entorno documentada en el README y en `env.template`, nunca un valor default hardcodeado en `application.properties`.
- Este ADR no reemplaza la rotación de la credencial ya expuesta en el historial de git — eso sigue siendo una acción manual pendiente en Atlas, independiente de este cambio de código.
