# API - Módulo P2: Piezas y digitalizaciones

Este documento explica los endpoints que desarrollé para gestionar las piezas y sus digitalizaciones.

## 1. Listar piezas

**GET /pieces**

Devuelve todas las piezas registradas, con paginación.

Parámetros:
- `page`: número de página (empieza en 0).
- `size`: cantidad de piezas por página (por defecto 10).

El tamaño permitido es de 1 a 100 y la página no puede ser negativa.

Ejemplo:

`GET /pieces?page=0&size=10`

## 2. Buscar una pieza por ID

**GET /pieces/{id}**

Permite consultar una pieza específica utilizando su ID.

Ejemplo:

`GET /pieces/1`

## 3. Listar digitalizaciones de una pieza

**GET /pieces/{id}/digitizations**

Devuelve las digitalizaciones asociadas a una pieza.

Ejemplo:

`GET /pieces/1/digitizations`

## 4. Registrar una pieza

**POST /pieces**

Permite registrar una pieza nueva enviando sus datos en formato JSON.

Los datos se reciben mediante `PieceRequestDTO` y se validan antes de registrarlos.

Si se registra correctamente, devuelve el código HTTP 201 (Created).

## 5. Agregar una digitalización

**POST /pieces/{id}/digitizations**

Permite agregar una digitalización a una pieza existente.

Los datos se reciben mediante `DigitizationRequestDTO` y se validan antes de registrarlos.

Si se registra correctamente, devuelve el código HTTP 201 (Created).

## Respuestas HTTP

- **200 OK:** la consulta se realizó correctamente.
- **201 Created:** se creó una pieza o digitalización.
- **400 Bad Request:** los parámetros de paginación son inválidos.
- **Otros errores:** se gestionan mediante el manejo de excepciones del proyecto.

## Tecnologías utilizadas

- Java
- Spring Boot
- Spring Data JPA
- API REST
- DTO para recibir y devolver datos
