# Diagramas de secuencia

Cada participante está marcado con su capa: **[Presentación]**, **[Servicio]**, **[Datos]** y
**[Dominio]**.

## 1. Catalogar una digitalización (US-10)

`PUT /digitizations/{digitizationId}/tags/{tagId}`

```mermaid
sequenceDiagram
    actor A as Archivista
    box Presentación
        participant C as CatalogingController
    end
    box Servicio
        participant S as CatalogingService
    end
    box Datos
        participant DR as DigitizationRepository
        participant TR as TagRepository
    end
    box Dominio
        participant D as Digitization
    end

    A->>C: PUT /digitizations/1/tags/2
    C->>S: assignTag(1, 2)
    S->>DR: findById(1)
    DR-->>S: Digitization
    S->>TR: findById(2)
    alt el tag no existe
        TR-->>S: vacío
        S-->>C: ResourceNotFoundException
        C-->>A: 404 {"error": "..."} (GlobalExceptionHandler)
    else el tag existe
        TR-->>S: Tag
        S->>D: getTags().add(tag)
        S->>DR: save(digitization)
        S-->>C: DigitizationTagsResponseDTO
        C-->>A: 200 + digitalización con sus tags
    end
```

## 2. Buscar piezas (US-12 a 14)

`GET /pieces/search?name=carta&tags=fotografia`

```mermaid
sequenceDiagram
    actor V as Visitante
    box Presentación
        participant C as PieceSearchController
    end
    box Servicio
        participant S as PieceSearchService
        participant M as PieceMapper
    end
    box Datos
        participant SP as PieceSpecifications
        participant PR as PieceRepository
    end

    V->>C: GET /pieces/search?name=carta&tags=fotografia
    C->>S: search(PieceSearchRequestDTO, page, size)
    S->>S: validar paginación y fechas, normalizar filtros
    S->>SP: withFilters(name, description, dateFrom, dateTo, tags)
    SP-->>S: Specification<Piece>
    S->>PR: findAll(specification, pageable)
    Note over PR: SELECT ... FROM pieces WHERE lower(name) LIKE '%carta%'<br/>AND EXISTS (digitalización de la pieza con el tag)
    PR-->>S: Page<Piece>
    S->>M: toPieceDto(piece) por cada pieza
    S-->>C: PageResponseDTO<PieceResponseDTO>
    C-->>V: 200 + página de piezas
```

## 3. Consultar desde Telegram (US-15 y 16)

```mermaid
sequenceDiagram
    actor V as Visitante
    participant T as Telegram
    box Presentación
        participant P as TelegramBotPoller
        participant H as TelegramBotHandler
        participant TC as TelegramClient
    end
    box Servicio
        participant SS as PieceSearchService
        participant PS as PieceService
    end

    V->>T: "carta"
    loop cada segundo (long polling)
        P->>TC: getUpdates(offset)
        TC->>T: GET /getUpdates
        T-->>TC: [update con "carta"]
    end
    P->>H: handle("carta")
    H->>SS: search(name = "carta", page 0, size 5)
    SS-->>H: PageResponseDTO
    H-->>P: texto con la lista y links /pieza_1
    P->>TC: sendMessage(chatId, texto)
    TC->>T: POST /sendMessage
    T-->>V: resultados

    V->>T: /pieza_1
    P->>H: handle("/pieza_1")
    H->>PS: getPieceById(1)
    PS-->>H: PieceResponseDTO
    H-->>P: nombre, fecha, descripción y URLs
    P->>TC: sendMessage(chatId, texto)
    T-->>V: detalle de la pieza
```
