# Diagrama de clases de dominio

Las tres entidades del paquete `model`. Los atributos son los que se persisten.

```mermaid
classDiagram
    class Piece {
        -Long id
        -String name
        -String description
        -LocalDate date
        -List~Digitization~ digitizations
    }

    class Digitization {
        -Long id
        -String url
        -String description
        -Piece piece
        -Set~Tag~ tags
    }

    class Tag {
        -Long id
        -String name
    }

    Piece "1" *-- "0..*" Digitization : tiene
    Digitization "0..*" --> "0..*" Tag : catalogada con
```

- **Composición Piece → Digitization**: una digitalización no existe sin su pieza; se guarda y se
  borra junto con ella (`cascade = ALL`, `orphanRemoval = true`).
- **Asociación Digitization → Tag (N a M)**: la navegación es en un solo sentido. `Tag` no conoce las
  digitalizaciones; para saber si un tag está en uso se consulta desde `TagRepository`.
- Los tags se asignan a digitalizaciones, pero la búsqueda devuelve piezas: una pieza "tiene" un tag si
  alguna de sus digitalizaciones lo tiene.
