# API - Archivo Digital

API REST del monolito. Todas las respuestas son JSON. Base: `http://localhost:8080`.

| Módulo            | Método   | Ruta                                         | Historia      |
| ----------------- | -------- | -------------------------------------------- | ------------- |
| Tags              | `POST`   | `/tags`                                      | US-01         |
| Tags              | `GET`    | `/tags`                                      | US-02         |
| Tags              | `PUT`    | `/tags/{id}`                                 | US-03         |
| Tags              | `DELETE` | `/tags/{id}`                                 | US-04         |
| Piezas            | `POST`   | `/pieces`                                    | US-05         |
| Piezas            | `GET`    | `/pieces/{id}`                               | US-06         |
| Piezas            | `GET`    | `/pieces?page=&size=`                        | US-07         |
| Digitalizaciones  | `POST`   | `/pieces/{id}/digitizations`                 | US-08         |
| Digitalizaciones  | `GET`    | `/pieces/{id}/digitizations`                 | US-09         |
| Catalogación      | `PUT`    | `/digitizations/{digitizationId}/tags/{tagId}` | US-10       |
| Catalogación      | `DELETE` | `/digitizations/{digitizationId}/tags/{tagId}` | US-11       |
| Búsqueda          | `GET`    | `/pieces/search`                             | US-12, 13, 14 |

## Errores

Todos los errores devuelven el mismo cuerpo:

```json
{ "error": "mensaje" }
```

| Código | Cuándo                                                                  |
| ------ | ----------------------------------------------------------------------- |
| 400    | Falla la validación del body, un parámetro tiene formato inválido o la paginación/fechas no son válidas |
| 404    | No existe la pieza, digitalización o tag pedido                         |
| 409    | Se viola una regla de negocio: nombre de tag repetido o borrar un tag en uso |

## 1. Tags

Body de entrada (`TagRequestDTO`): `{ "name": "fotografia" }`. El nombre es obligatorio, de hasta 100
caracteres y único.

Respuesta (`TagResponseDTO`): `{ "id": 1, "name": "fotografia" }`.

- **`POST /tags`** crea un tag. Devuelve `201`. Si el nombre ya existe, `409`; si viene vacío, `400`.
- **`GET /tags`** lista todos los tags. Devuelve `200` con un array.
- **`PUT /tags/{id}`** renombra un tag. Como las digitalizaciones guardan la referencia al tag (no el
  texto), el cambio se ve en todas. `404` si no existe, `409` si el nombre nuevo ya está usado.
- **`DELETE /tags/{id}`** elimina un tag. Devuelve `204`. `404` si no existe y `409` si está asignado a
  alguna digitalización (decisión del grupo: un tag en uso no se puede borrar).

## 2. Piezas

Body de entrada (`PieceRequestDTO`):

```json
{
  "name": "Carta de San Martín",
  "description": "Carta manuscrita",
  "date": "1817-05-10"
}
```

`name` y `date` son obligatorios. `date` es la fecha del objeto físico, en formato `AAAA-MM-DD`.

Respuesta (`PieceResponseDTO`):

```json
{
  "id": 1,
  "name": "Carta de San Martín",
  "description": "Carta manuscrita",
  "date": "1817-05-10",
  "digitizations": [
    { "id": 1, "url": "https://ejemplo.com/carta-frente.jpg", "description": "Frente" }
  ]
}
```

- **`POST /pieces`** da de alta una pieza. Devuelve `201` con la pieza y su `id`.
- **`GET /pieces/{id}`** devuelve la pieza con sus digitalizaciones. `404` si no existe.
- **`GET /pieces?page=0&size=10`** lista las piezas paginadas. `page` empieza en 0 (por defecto 0) y
  `size` va de 1 a 100 (por defecto 10); fuera de esos rangos devuelve `400`. Devuelve
  `{ content, page, size, totalElements, totalPages }`, igual que la búsqueda.

## 3. Digitalizaciones

Body de entrada (`DigitizationRequestDTO`):

```json
{ "url": "https://ejemplo.com/carta-frente.jpg", "description": "Frente" }
```

La `url` es obligatoria.

- **`POST /pieces/{id}/digitizations`** agrega una digitalización a la pieza. Devuelve `201` con
  `{ id, url, description }`. `404` si la pieza no existe.
- **`GET /pieces/{id}/digitizations`** lista las digitalizaciones de la pieza. `404` si la pieza no
  existe.

## 4. Catalogación

No tiene body: el tag y la digitalización van en la ruta. Solo se pueden usar tags que ya existen.

- **`PUT /digitizations/{digitizationId}/tags/{tagId}`** asigna el tag. Si ya estaba asignado no hace
  nada (es idempotente).
- **`DELETE /digitizations/{digitizationId}/tags/{tagId}`** quita el tag.

Ambos devuelven `200` con la digitalización y sus tags (`DigitizationTagsResponseDTO`), o `404` si no
existe la digitalización o el tag:

```json
{
  "id": 1,
  "url": "https://ejemplo.com/carta-frente.jpg",
  "description": "Frente",
  "tags": [ { "id": 1, "name": "fotografia" } ]
}
```

## 5. Búsqueda de piezas

**`GET /pieces/search`**

Busca **piezas** (nunca digitalizaciones). Todos los filtros son opcionales y se combinan con Y.

| Parámetro     | Ejemplo               | Qué hace                                                                 |
| ------------- | --------------------- | ------------------------------------------------------------------------ |
| `name`        | `carta`               | El nombre contiene el texto (sin distinguir mayúsculas)                  |
| `description` | `manuscrita`          | La descripción contiene el texto (sin distinguir mayúsculas)             |
| `dateFrom`    | `1800-01-01`          | Fecha de la pieza mayor o igual                                          |
| `dateTo`      | `1899-12-31`          | Fecha de la pieza menor o igual                                          |
| `tags`        | `tags=foto&tags=carta` | La pieza debe tener **todos** los tags, en cualquiera de sus digitalizaciones |
| `page`        | `0`                   | Página, desde 0 (por defecto 0)                                          |
| `size`        | `10`                  | Tamaño de página, de 1 a 100 (por defecto 10)                            |

También se acepta `tags=foto,carta`. Los resultados se ordenan por nombre. Si no hay resultados
devuelve `200` con `content` vacío, no un error. Devuelve `400` si `dateFrom` es posterior a `dateTo`,
si una fecha no tiene formato `AAAA-MM-DD` o si la paginación es inválida.

Ejemplo: `GET /pieces/search?name=carta&dateFrom=1800-01-01&tags=fotografia&page=0&size=10`

```json
{
  "content": [
    {
      "id": 1,
      "name": "Carta de San Martín",
      "description": "Carta manuscrita",
      "date": "1817-05-10",
      "digitizations": [
        { "id": 1, "url": "https://ejemplo.com/carta-frente.jpg", "description": "Frente" }
      ]
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

## 6. Bot de Telegram

No es parte de la API REST, pero usa los mismos services. Ver la sección del bot en el
[`README`](../README.md) y la prueba en [`test.md`](test.md).
