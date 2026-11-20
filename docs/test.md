# Prueba del sistema (recorrido feliz)

Secuencia para probar todo de punta a punta: crear tags, crear una pieza, agregarle
digitalizaciones, catalogarlas, buscar y ver el resultado en el bot.

## Antes de empezar

1. Levantar la app con `./mvnw spring-boot:run` (ver [`README`](../README.md)). La base arranca vacía,
   así que los `id` de abajo son los que se generan si se sigue la secuencia en orden.
2. Los comandos usan `curl` desde una terminal bash (Linux, Mac o **Git Bash** en Windows). En
   PowerShell conviene usar Postman con los mismos datos, porque las comillas del JSON se escapan
   distinto.

```bash
API=http://localhost:8080
```

## 1. Tags (US-01 a 04)

```bash
# Crear dos tags -> 201, ids 1 y 2
curl -i -X POST $API/tags -H "Content-Type: application/json" -d '{"name":"fotografia"}'
curl -i -X POST $API/tags -H "Content-Type: application/json" -d '{"name":"carta"}'

# Nombre repetido -> 409
curl -i -X POST $API/tags -H "Content-Type: application/json" -d '{"name":"carta"}'

# Listar -> 200 con los dos
curl -i $API/tags

# Renombrar -> 200
curl -i -X PUT $API/tags/2 -H "Content-Type: application/json" -d '{"name":"correspondencia"}'

# Crear uno de prueba y borrarlo -> 201 (id 3) y 204
curl -i -X POST $API/tags -H "Content-Type: application/json" -d '{"name":"borrar"}'
curl -i -X DELETE $API/tags/3
```

## 2. Piezas y digitalizaciones (US-05 a 09)

```bash
# Alta de dos piezas -> 201, ids 1 y 2
curl -i -X POST $API/pieces -H "Content-Type: application/json" \
  -d '{"name":"Carta de San Martin","description":"Carta manuscrita","date":"1817-05-10"}'
curl -i -X POST $API/pieces -H "Content-Type: application/json" \
  -d '{"name":"Mapa de Buenos Aires","description":"Plano de la ciudad","date":"1890-01-01"}'

# Pieza sin nombre -> 400
curl -i -X POST $API/pieces -H "Content-Type: application/json" -d '{"date":"1900-01-01"}'

# Digitalizaciones -> 201, ids 1, 2 y 3
curl -i -X POST $API/pieces/1/digitizations -H "Content-Type: application/json" \
  -d '{"url":"https://ejemplo.com/carta-frente.jpg","description":"Frente"}'
curl -i -X POST $API/pieces/1/digitizations -H "Content-Type: application/json" \
  -d '{"url":"https://ejemplo.com/carta-dorso.jpg","description":"Dorso"}'
curl -i -X POST $API/pieces/2/digitizations -H "Content-Type: application/json" \
  -d '{"url":"https://ejemplo.com/mapa.jpg","description":"Escaneo completo"}'

# Consultar -> 200; pieza inexistente -> 404
curl -i $API/pieces/1
curl -i $API/pieces/99
curl -i $API/pieces/1/digitizations
curl -i "$API/pieces?page=0&size=10"
```

## 3. Catalogación (US-10, 11)

```bash
# Carta: frente con "fotografia" y dorso con "correspondencia". Mapa: "fotografia"
curl -i -X PUT $API/digitizations/1/tags/1
curl -i -X PUT $API/digitizations/2/tags/2
curl -i -X PUT $API/digitizations/3/tags/1

# Tag que no existe -> 404 (no se pueden inventar tags)
curl -i -X PUT $API/digitizations/1/tags/99

# Borrar un tag en uso -> 409
curl -i -X DELETE $API/tags/1

# Quitar y volver a poner -> 200
curl -i -X DELETE $API/digitizations/3/tags/1
curl -i -X PUT $API/digitizations/3/tags/1
```

## 4. Búsqueda (US-12 a 14)

```bash
# Por nombre, sin distinguir mayúsculas -> la carta
curl -s "$API/pieces/search?name=CARTA"

# Sin resultados -> 200 con content vacío
curl -s "$API/pieces/search?name=inexistente"

# Por descripción y rango de fechas -> el mapa
curl -s "$API/pieces/search?description=plano&dateFrom=1850-01-01&dateTo=1900-12-31"

# Por un tag -> las dos piezas (cada una tiene "fotografia" en alguna digitalización)
curl -s "$API/pieces/search?tags=fotografia"

# Por dos tags -> solo la carta (es la única que tiene los dos)
curl -s "$API/pieces/search?tags=fotografia&tags=correspondencia"

# Paginado -> 1 resultado en content, totalElements 2
curl -s "$API/pieces/search?page=0&size=1"

# Fechas al revés -> 400
curl -i "$API/pieces/search?dateFrom=1900-01-01&dateTo=1800-01-01"
```

## 5. Bot de Telegram (US-15, 16)

Con la app levantada con el token del bot (ver README) y los datos cargados arriba, escribirle al bot:

| Mensaje                | Respuesta esperada                                               |
| ---------------------- | ---------------------------------------------------------------- |
| `/start`               | La ayuda con los comandos                                        |
| `carta`                | "Encontré 1 pieza(s)..." con la carta y el link `/pieza_1`       |
| `/tag fotografia`      | La carta y el mapa                                               |
| `/pieza_1` (tocar el link) | Nombre, fecha, descripción y las URLs del frente y el dorso  |
| `/pieza 99`            | "No existe la pieza 99."                                         |

## 6. Tests automáticos

```bash
./mvnw test
```

| Test                          | Qué prueba                                                         |
| ----------------------------- | ------------------------------------------------------------------ |
| `TagServiceTest`              | Alta, renombrado y borrado de tags, con la regla de tag en uso     |
| `PieceServiceTest`            | Alta y consulta de piezas y digitalizaciones                       |
| `CatalogingServiceTest`       | Asignar y quitar tags                                              |
| `PieceSearchServiceTest`      | Validaciones y paginación de la búsqueda (repositorio mockeado)    |
| `PieceSearchIntegrationTest`  | La consulta real contra H2: nombre, descripción, fechas y tags     |
| `TelegramBotHandlerTest`      | Comandos del bot y formato de las respuestas                       |
| `TpApplicationTests`          | Que el contexto de Spring levanta                                  |
