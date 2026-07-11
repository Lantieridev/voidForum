# 0001 — MongoDB como document store para hilos de discusión

## Estado
Aceptada

## Contexto
voidForum nace como proyecto académico de la curricula de Database II, con el requisito explícito de usar una base NoSQL. Más allá del requisito, el dominio (posts con hilos de comentarios anidados de profundidad variable) encaja naturalmente con documentos: un comentario puede ser respuesta de otro (`parentCommentId`) sin un límite de profundidad fijo, y no hay necesidad de joins complejos para mostrar un post con sus reacciones.

## Decisión
- MongoDB Atlas como única base de datos, vía Spring Data MongoDB (`@Document`, repositorios `MongoRepository`).
- Los hilos de comentarios se modelan como una colección plana (`comments`) con `parentCommentId` nullable en vez de documentos anidados embebidos — permite indexar por `postId` (`@Indexed` en `Comment.postId`) y paginar/cargar el hilo de un post sin traer documentos gigantes.
- `Post`, `User`, `Vote` son colecciones separadas, relacionadas por id de string (no ObjectId tipado) para simplificar el mapeo a DTOs.

## Consecuencias
- No hay transacciones multi-documento configuradas — una operación que necesite atomicidad entre colecciones (ej. borrar un post y todos sus comentarios/votos) no está garantizada como atómica hoy. Si se agrega una feature así, es el momento de introducir `@Transactional` con MongoDB o reevaluar el modelo.
- Sin validación de integridad referencial a nivel de base (no hay FKs) — un `postId` huérfano no lo detecta MongoDB, hay que validarlo en el service layer si se vuelve un problema real.
- `auto-index-creation=false` en `application.properties`: los índices (como el de `Comment.postId`) no se crean solos al levantar la app. Si se agrega un `@Indexed` nuevo, hay que crear el índice manualmente en Atlas o revisar esta config.
