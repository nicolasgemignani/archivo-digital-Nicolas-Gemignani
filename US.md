
Son  **3 entidades** :

* **Piece** (pieza física): `id`, `name`, `description`, `date`
* **Digitization** : `id`, `url`, `description`. Pertenece a **una** Piece.
* **Tag** : `id`, `name` (único)

Las relaciones son:

* Una Piece tiene muchas Digitizations (1 a N).
* Una Digitization tiene muchos Tags, y un Tag puede estar en muchas Digitizations (N a M).

Los nombres van en inglés porque lo pide el `AGENTS.md`.

### Épica 1: Tags (los administra un administrador)

**US-01 Crear tag.** Como administrador, quiero crear un tag, para ampliar el conjunto con el que se cataloga.

* Criterios: se crea con nombre; si el nombre ya existe o viene vacío, devuelve error.

**US-02 Listar tags.** Como archivista, quiero ver los tags disponibles, para elegir cuál asignar.

* Criterios: devuelve todos los tags existentes.

**US-03 Renombrar tag.** Como administrador, quiero cambiar el nombre de un tag, para corregirlo sin perder las asignaciones.

* Criterios: el cambio se refleja en todas las digitalizaciones; no permite un nombre repetido.

**US-04 Eliminar tag.** Como administrador, quiero eliminar un tag que ya no sirve, para mantener ordenado el conjunto.

* Criterios: si no existe, error 404; **regla a decidir** sobre qué pasa si está en uso (ver preguntas abiertas).

### Épica 2: Digitalización

**US-05 Alta de pieza.** Como archivista, quiero dar de alta una pieza física con nombre, descripción y fecha, para registrarla en el archivo.

* Criterios: nombre obligatorio; devuelve la pieza creada con su id.

**US-06 Consultar una pieza.** Como archivista, quiero ver una pieza con sus digitalizaciones, para revisar qué se digitalizó.

* Criterios: si el id no existe, error 404.

**US-07 Listar piezas paginadas.** Como archivista, quiero listar las piezas por páginas, porque van a ser muchas.

* Criterios: acepta `page` y `size`; nunca devuelve todo junto.

**US-08 Agregar digitalización a una pieza.** Como archivista, quiero asociar una digitalización (url + descripción) a una pieza, para registrar su recurso digital.

* Criterios: la pieza debe existir; la url es obligatoria.

**US-09 Listar digitalizaciones de una pieza.** Como archivista, quiero ver las digitalizaciones de una pieza, para encontrar un recurso puntual.

### Épica 3: Catalogación

**US-10 Asignar tag a una digitalización.** Como archivista, quiero asociar un tag existente a una digitalización, para clasificarla.

* Criterios: el tag y la digitalización deben existir; no se pueden inventar tags nuevos desde acá.

**US-11 Quitar tag de una digitalización.** Como archivista, quiero quitar un tag asignado, para corregir errores de catalogación.

### Épica 4: Disponibilización (búsqueda de piezas, no de digitalizaciones)

**US-12 Buscar piezas por nombre.** Como visitante, quiero buscar piezas por nombre, para encontrar lo que me interesa.

* Criterios: devuelve piezas paginadas; una búsqueda sin resultados devuelve lista vacía, no error.

**US-13 Filtrar también por descripción y fecha.** Como visitante, quiero filtrar por descripción y por rango de fechas, para afinar la búsqueda.

**US-14 Filtrar también por tags.** Como visitante, quiero filtrar por uno o más tags, para ver piezas de cierta categoría.

* Criterios: una pieza coincide si alguna de sus digitalizaciones tiene el tag.

### Épica 5: Visualización (bot de Telegram)

**US-15 Buscar desde Telegram.** Como visitante, quiero escribirle al bot un texto de búsqueda y recibir resultados, para consultar el archivo desde el celular.

**US-16 Ver el detalle de una pieza en Telegram.** Como visitante, quiero que el bot me muestre nombre, descripción y links de las digitalizaciones, para llegar al recurso.

### Épica 6: Transversal (no son historias puras, son habilitadores)

**E-01 Esqueleto de entidades.** Crear `Piece`, `Digitization` y `Tag` con sus relaciones.  **Es la tarea que más choques de git genera** , así que la hace una sola persona, primero.

**E-02 Manejo de errores.** `GlobalExceptionHandler` y excepciones base (404, validación). Lo usan todos.

**E-03 Documentación.** `api.md`, `test.md`, diagramas (clases, arquitectura, base de datos, secuencia) y README.

### Chequeo INVEST

| Historia   | Indep.  | Negociable | Valiosa   | Estimable | Pequeña | Testeable | Nota                                    |
| ---------- | ------- | ---------- | --------- | --------- | -------- | --------- | --------------------------------------- |
| US-01 a 03 | Sí     | Sí        | Sí       | Sí       | Sí      | Sí       |                                         |
| US-04      | Sí     | Sí        | Sí       | Parcial   | Sí      | Sí       | Falta decidir la regla del tag en uso   |
| US-05 a 07 | Sí     | Sí        | Sí       | Sí       | Sí      | Sí       |                                         |
| US-08, 09  | Parcial | Sí        | Sí       | Sí       | Sí      | Sí       | Dependen de que exista la pieza (US-05) |
| US-10, 11  | Parcial | Sí        | Sí       | Sí       | Sí      | Sí       | Dependen de US-01 y US-08               |
| US-12 a 14 | Parcial | Sí        | Sí       | Sí       | Sí      | Sí       | Tocan el mismo endpoint, van en orden   |
| US-15, 16  | Parcial | Sí        | Sí       | Sí       | Sí      | Sí       | Dependen de la búsqueda (US-12)        |
| E-01 a 03  | No      | n/a        | Indirecta | Sí       | Sí      | Parcial   | Son habilitadores, no historias         |
