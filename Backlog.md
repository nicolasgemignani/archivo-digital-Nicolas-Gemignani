
# Backlog - Archivo Digital

Entrega: 16/11/2026 (meta interna: código terminado el 09/11)

## Decisiones de dominio

- Piece.date = fecha del objeto físico, cargada por el archivista (confirmar con profes)
- No se puede borrar un Tag en uso (error)
- Búsqueda con varios tags: la pieza debe tener TODOS
- La búsqueda devuelve Piece, nunca Digitization

## Reparto

- P1 (Nicolás): E-01, E-02, Tags (US-01..04), Catalogación (US-10, 11)
- P2: Piece y Digitization (US-05..09)
- P3: Búsqueda (US-12..14) y Bot (US-15, 16)
- Docs: ver Fase 4

## Fase 0 - Base compartida (P1, hasta 01/11). BLOQUEA A LOS DEMÁS

- [ ] E-01: entidades model Tag, Piece, Digitization con relaciones
  - Archivos: model/Tag.java, model/Piece.java, model/Digitization.java
- [ ] E-02: exception/GlobalExceptionHandler.java y excepción NotFound base
- [ ] Mergear a main y avisar al grupo
- [ ] Todos: verificar que compila (JDK 21 + toolchains.xml)

## Fase 1 - Cada uno lo suyo (02/11 - 08/11), en paralelo

### P1: Tags y catalogación

- [ ] repository/TagRepository, dto/Tag*DTO, service/TagService, controller/TagController
- [ ] US-01 a 04 (CRUD de tags, con regla de tag en uso)
- [ ] US-10, 11 (asignar/quitar tag): va en la última parte, requiere que P2 haya mergeado Digitization
- [ ] Test unitario de TagService

### P2: Piezas y digitalizaciones

- [ ] repository/PieceRepository, DigitizationRepository, DTOs, services, controllers
- [ ] US-05 a 09 (con paginación)
- [ ] Test unitario de PieceService

### P3: Búsqueda

- [ ] repository: consulta de piezas por filtros (nombre, descripción, fechas, tags)
- [ ] service/PieceSearchService, controller/PieceSearchController, DTOs
- [ ] US-12 (nombre) primero, luego US-13 y US-14
- [ ] Test unitario de PieceSearchService

## Fase 2 - Integración (09/11 - 10/11)

- [ ] Mergear todo a main, resolver conflictos
- [ ] Recorrido feliz completo (crear tag, pieza, digitalización, catalogar, buscar)

## Fase 3 - Bot (10/11 - 12/11) - P3

- [ ] Bot de Telegram: US-15 (buscar) y US-16 (detalle)

## Fase 4 - Documentación (hasta 14/11)

- [ ] README: grupo, integrantes, cómo levantar (P1)
- [ ] docs/api.md (P2)
- [ ] docs/test.md (P3)
- [ ] ARCHITECTURE.md + diagrama de clases de dominio (P1)
- [ ] Diagrama de base de datos (P2)
- [ ] Diagramas de secuencia con capas marcadas (P3)
- [ ] Cada uno repasa SU parte y entiende el resto

## Extras (solo si sobra tiempo)

- [ ] Postgres / Render
- [ ] OCR externo
- [ ] Batch CSV
