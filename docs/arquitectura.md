# Arquitectura

Monolito Spring Boot con una única base de datos. Más detalle (actores, alcance, glosario) en
[`ARCHITECTURE.md`](../ARCHITECTURE.md).

## Contenedores (C4 nivel 2)

```mermaid
flowchart LR
    archivist["Archivista / Administrador<br/>(Postman, curl)"]
    visitor["Visitante"]
    telegram["Telegram<br/>(Bot API)"]

    subgraph archive["Archivo Digital (monolito)"]
        app["Aplicación Spring Boot<br/>Java 21<br/>API REST + bot"]
        db[("H2 en memoria")]
    end

    archivist -->|HTTP/JSON| app
    visitor -->|escribe al bot| telegram
    app -->|"HTTPS: getUpdates / sendMessage"| telegram
    app -->|JDBC / JPA| db
```

El bot corre **dentro del mismo proceso**: la app le pregunta a Telegram cada pocos segundos si hay
mensajes nuevos (long polling). Por eso no necesita una URL pública ni un webhook, y funciona en una PC.

## Capas (Fowler)

```mermaid
flowchart TD
    rest["Cliente HTTP"] --> controller
    tg["Telegram"] --> bot

    subgraph presentation["Presentación"]
        controller["controller<br/>TagController, PieceController,<br/>CatalogingController, PieceSearchController"]
        bot["bot<br/>TelegramBotPoller, TelegramBotHandler"]
    end

    subgraph servicelayer["Servicio"]
        service["service<br/>TagService, PieceService,<br/>CatalogingService, PieceSearchService"]
    end

    subgraph domain["Dominio"]
        model["model<br/>Piece, Digitization, Tag"]
    end

    subgraph data["Acceso a datos"]
        repository["repository<br/>TagRepository, PieceRepository,<br/>DigitizationRepository, PieceSpecifications"]
    end

    db[("H2")]
    client["bot/TelegramClient<br/>(gateway a Telegram)"]

    controller --> service
    bot --> service
    bot --> client
    service --> repository
    service -.usa.-> model
    repository --> db
```

| Capa            | Paquete                       | Responsabilidad                                                       |
| --------------- | ----------------------------- | --------------------------------------------------------------------- |
| Presentación    | `controller`, `bot`           | Traducir HTTP o un mensaje de Telegram a una llamada al service, y la respuesta a JSON o texto. |
| Servicio        | `service`                     | Orquestar: buscar entidades, aplicar reglas, persistir y mapear a DTO. |
| Dominio         | `model`, `model/exceptions`   | Entidades y reglas de negocio.                                        |
| Acceso a datos  | `repository`                  | Consultas a la base con Spring Data JPA.                              |
| Transversal     | `dto`, `exception`            | Objetos de transporte y traducción de errores a HTTP.                 |

`TelegramBotHandler` cumple para el bot el mismo papel que un controller para la API REST: no tiene
lógica de negocio, solo llama a `PieceSearchService` y `PieceService` y arma el texto de respuesta.
`TelegramClient` es un *gateway*: encapsula la comunicación HTTP con un sistema externo.
