## TP Integrador Diseño de Software

Partiendo del Caso de “Archivo Digital” realizado en Ingeniería del Software I, vamos [URL 🔗](https://docs.google.com/document/d/1ecjR-j_M1CVzVMlwAqICq8hSFjvWUtX6eEHTbZfMfkg/edit?usp=sharing)

a realizar una implementación acotada del mismo, poniendo en práctica lo visto en clase. Todo con grupos de 4 personas.

## Alcance

- \- El proceso principal del Archivo Digital consiste en

- \- Digitalización

- \- Alta de la pieza o material físico.

- \- Asociar “digitalizaciones” al mismo. La misma no es más que una url con un recurso y una descripción.

- \- Catalogación

- \- Asociar tags a las digitalizaciones

- \- Los tags no son libres, se seleccionan de un conjunto de tags, que un administrador puede cambiar en tiempo de ejecución

- \- Disponibilización

- \- Permitir búsquedas por fecha, nombre, descripción y tags sobre el archivo digital.

- \- Debemos asumir que van a ser muchas piezas

- \- Las búsquedas son de las piezas y no de las digitalizaciones

- \- Visualización

- \- Vamos a hacer un Bot de telegram que permite hacer búsquedas y mostrar los resultados.

## Detalle técnico

- \- Debe resolverse todo en una arquitectura de monolito, es decir un único componente y una única base de datos (la que quieran). No hace falta que repitan la solución que hicieron en ISW (salvo que realmente sepan lo que están haciendo y se la banquen después).

- \- Digitalización, Catalogación y Disponibilización son APIs (nada de pantallas).

- \- Se recomienda el uso de Java, Spring, Spring Boot y bases relacionales, como se hicieron las prácticas (pueden usar la H2 si quieren).

- \- Se deben seguir las capas de Fowler vistas en clases, usando las convenciones, nombres y responsabilidades vistas en clases.


## Entrega

- \- La implementación del proyecto debe estar en Github, con un archivo readme que indique el nombre del grupo, los integrantes y una explicación de como levantar la aplicación

- \- Al crear el repositorio:

- \- Lo más importante, es que deben usar como template el repositorio https://github.com/ddso-unsam/tp-repo-template

- \- O lo dejan público o nos agregan en el equipo/team.

- \- el repositorio debe tener un directorio /docs/ que tenga:

- \- diagramas de clases de dominio y otro/s de arquitectura.

- \- diagramas de bases de datos.

- \- diagramas de secuencia (los que consideren más importantes) y marquen a qué capa pertenece cada clase.

- \- un api.md con la especificacion de la api web.

- \- un test.md que ponga una secuencia de uso para probar todo el sistema, por ejemplo, crear un tag, crear una pieza, crear una digitalización, categorizar las digitalización con el tag y buscarlo.

## Evaluación

- \- Primero que nada la aplicación tiene que funcionar, puede fallar eventualmente, pero al menos tenemos que poder hacer una recorrida feliz de todo.

- \- Están invitados a usar la IA de su preferencia (de hecho deberían hacerlo fuertemente), sin embargo, durante la evaluación se va a hacer hincapié en que comprendan lo que hicieron, la documentación el código, etc

- \- Hay mucho de documentación, no sirve para nada que le digan a la ia que escriba un documento / markdown de 850 páginas, vean los títulos, entiendan el diagrama que se hace, discutanlo, ejecutenlo, etc, van a ser fuertemente evaluados sobre eso.

- \- Todos deben tener al menos un commit en el repo, que aporte al trabajo, no cuenta si solo suben documentación.

- \- Todos tienen que subir algun test unitario de un service y tener preparado para explicarlo.

- \- La nota es individual y no grupal.


## Extras (no obligatorio)

- 1. Desplegar el TP En Render con una base Postgres

- 2. Usar servicios de 3ros / externos para el etiquetado: yo encontre este para el OCR y este para el etiquetado de imágenes. [URL 🔗](https://ocr.space/ocrapi)

- 3. Hacer un proceso batch para cargar un csv de piezas, digitalizaciones y anotaciones.

- 4. Combinar 2 y 3.
