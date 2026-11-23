
# N.R.V

TP Integrador de Diseño de Software (UNSAM): implementación acotada del caso **Archivo Digital**.

El sistema permite dar de alta piezas físicas, asociarles digitalizaciones (una URL con una descripción), catalogarlas con tags de un conjunto administrable y buscar piezas por fecha, nombre, descripción y tags. La visualización se hace desde un bot de Telegram.

## Integrantes

| # | Nombre             | Usuario de GitHub                                      |
| - | ------------------ | ------------------------------------------------------ |
| 1 | Nicolás Gemignani | [nicolasgemignani](https://github.com/nicolasgemignani) |
| 2 | Rodrigo Ojeda      | [RodryOjeda27](https://github.com/RodryOjeda27)         |

## Tecnologías

- Java 21
- Spring Boot 4.1 (Spring Web MVC, Spring Data JPA, Validation)
- H2 (base en memoria)
- Lombok
- JUnit 5 + Mockito
- Maven (con Maven Wrapper)

## Arquitectura

Monolito: un único componente y una única base de datos. El código está organizado **por capa** bajo `ar.edu.unsam.ddso.tp`:

| Paquete              | Responsabilidad                                                           |
| -------------------- | ------------------------------------------------------------------------- |
| `controller`       | Endpoints REST. Traducen HTTP a llamadas al service.                      |
| `dto`              | `record`s de entrada y salida de la API.                                |
| `model`            | Entidades JPA (`Piece`, `Digitization`, `Tag`) y reglas de negocio. |
| `model/exceptions` | Excepciones de reglas de negocio.                                         |
| `exception`        | Excepciones de "no encontrado" y `GlobalExceptionHandler`.              |
| `repository`       | Acceso a datos.                                                           |
| `service`          | Orquestación de la lógica de negocio.                                   |

Las convenciones completas están en [`AGENTS.md`](AGENTS.md) y la descripción del dominio en [`ARCHITECTURE.md`](ARCHITECTURE.md).

## Requisitos previos

1. **JDK 21** (por ejemplo Temurin). El proyecto usa el plugin de *toolchains* de Maven, que exige encontrar un JDK 21 aunque tengas otra versión instalada.
2. Un archivo `toolchains.xml` en tu carpeta de usuario de Maven (`C:\Users\TU-USUARIO\.m2\toolchains.xml` en Windows, `~/.m2/toolchains.xml` en Linux/Mac):

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <toolchains>
     <toolchain>
       <type>jdk</type>
       <provides>
         <version>21</version>
       </provides>
       <configuration>
         <jdkHome>RUTA/A/TU/JDK-21</jdkHome>
       </configuration>
     </toolchain>
   </toolchains>
   ```

   Si Maven falla con `No toolchain found for type jdk`, el problema está acá.

No hace falta instalar Maven ni una base de datos: se usa el Maven Wrapper y H2 en memoria.

## Cómo levantar la aplicación

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/nicolasgemignani/archivo-digital-Nicolas-Gemignani.git
   cd archivo-digital-Nicolas-Gemignani
   ```
2. Compilar:

   ```bash
   ./mvnw clean compile
   ```

   En Windows (PowerShell) usar `.\mvnw.cmd` en lugar de `./mvnw`.
3. Ejecutar:

   ```bash
   ./mvnw spring-boot:run
   ```

   La API queda disponible en `http://localhost:8080`. Se detiene con `Ctrl + C`.

Se usa el perfil `local` por defecto (H2 en memoria). **Los datos se pierden al detener la aplicación.**

### Consola de la base de datos

Con la app corriendo: `http://localhost:8080/h2-console`

| Campo    | Valor              |
| -------- | ------------------ |
| JDBC URL | `jdbc:h2:mem:tp` |
| User     | `SA`             |
| Password | *(vacío)*       |

## Cómo correr los tests

```bash
./mvnw test
```

## Chequeos de calidad

El proyecto incluye Checkstyle, PMD y SpotBugs:

```bash
./mvnw checkstyle:check
```

## Documentación

| Documento                             | Contenido                                       |
| ------------------------------------- | ----------------------------------------------- |
| [`US.md`](US.md)                     | Historias de usuario                            |
| [`Backlog.md`](Backlog.md)           | Backlog y reparto de tareas                     |
| [`ARCHITECTURE.md`](ARCHITECTURE.md) | Dominio, vistas C4 y diagrama de clases         |
| [`AGENTS.md`](AGENTS.md)             | Convenciones de código                         |
| [`docs/api.md`](docs/api.md)         | Especificación de la API web                    |
| [`docs/test.md`](docs/test.md)       | Secuencia de prueba de todo el sistema          |
| [`docs/diagrama-clases.md`](docs/diagrama-clases.md) | Diagrama de clases de dominio   |
| [`docs/arquitectura.md`](docs/arquitectura.md) | Contenedores y capas                  |
| [`docs/base-de-datos.md`](docs/base-de-datos.md) | Diagrama de base de datos           |
| [`docs/diagramas-secuencia.md`](docs/diagramas-secuencia.md) | Diagramas de secuencia con capas |

## Bot de Telegram

El bot permite buscar piezas y ver sus digitalizaciones desde Telegram (US-15 y US-16). Corre dentro
de la misma aplicación y consulta a Telegram por *long polling*, así que no hace falta una URL pública.

**Es opcional**: si no se configura un token, la aplicación arranca igual y funciona solo como API REST.

### Cómo activarlo

1. En Telegram, hablarle a [@BotFather](https://t.me/BotFather), mandar `/newbot` y seguir los pasos.
   Al final entrega un token con la forma `123456789:ABC...`. **No subir el token al repositorio.**
2. Definir la variable de entorno `TELEGRAM_BOT_TOKEN` y levantar la app en la misma terminal:

   ```bash
   # Linux / Mac / Git Bash
   export TELEGRAM_BOT_TOKEN=123456789:ABC...
   ./mvnw spring-boot:run
   ```

   ```powershell
   # Windows PowerShell
   $env:TELEGRAM_BOT_TOKEN="123456789:ABC..."
   .\mvnw.cmd spring-boot:run
   ```

3. Buscar el bot en Telegram por el nombre de usuario que se eligió y escribirle `/start`.

### Comandos

| Mensaje           | Qué hace                                                          |
| ----------------- | ----------------------------------------------------------------- |
| `/buscar <texto>` | Busca piezas por nombre (muestra hasta 5)                         |
| *texto suelto*    | Lo mismo que `/buscar`                                            |
| `/tag <nombre>`   | Piezas que tienen ese tag en alguna digitalización                |
| `/pieza <id>`     | Detalle: nombre, fecha, descripción y links de las digitalizaciones. Los resultados de búsqueda traen el link `/pieza_<id>` para tocarlo directo |
| `/start`, `/help` | Ayuda                                                             |

Si el bot no responde, revisar los logs: un token inválido aparece como error `401` y un webhook
configurado en el bot aparece como error `409`.

## Estructura del proyecto

```
src/main/java/ar/edu/unsam/ddso/tp/
├── TpApplication.java      # entry point
├── bot/                    # bot de Telegram (polling, handler y cliente HTTP)
├── controller/             # endpoints REST
├── dto/                    # records de request/response
├── model/                  # entidades y reglas de negocio
│   └── exceptions/         # excepciones de negocio
├── exception/              # not found + GlobalExceptionHandler
├── repository/             # acceso a datos
└── service/                # lógica de negocio
src/main/resources/
├── application.properties        # nombre de la app + perfil activo
├── application-local.properties  # H2 + JPA para desarrollo local
└── application-prod.properties   # H2 + JPA para "prod"
src/test/java/ar/edu/unsam/ddso/tp/
└── ...                           # tests unitarios, de integración de la búsqueda y smoke test
docs/                             # API, prueba del sistema y diagramas
```
