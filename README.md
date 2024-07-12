<p align="center">
  <img src="/img/forohub.jpg" alt="logo forohub"/>
</p>

<div align="center">

   ![Estado del Proyecto](https://img.shields.io/badge/Estado-Terminado-green)
   ![PRs Welcome](https://img.shields.io/badge/PRs-welcome-green)
   ![GitHub Org's stars](https://img.shields.io/github/stars/fica-millan?style=social)
   <br>
   ![Java Version](https://img.shields.io/badge/Java-17-orange)
   ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.6.5-brightgreen)
   ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
   ![Swagger](https://img.shields.io/badge/Swagger-3.0.0-red)
   ![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-2024.1-orange)
   ![Insomnia](https://img.shields.io/badge/Insomnia-9.3.2-lightgrey)
</div>

<h1 align="center">Foro Hub API</h1>


## Tabla de Contenidos
- [Descripción](#descripción)
- [Funcionalidades](#funcionalidades)
- [Demostración](#demostración)
- [Acceso](#acceso)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Código de Ejemplo](#código-de-ejemplo)
- [Contribución](#contribución)
- [Agradecimientos](#agradecimientos)
- [Autor](#autor)

## Descripción
La API de Foro Hub es una aplicación backend desarrollada para facilitar funcionalidades de 
foros de discusión. Construida con Java y Spring Boot, proporciona endpoints RESTful robustos para 
gestionar tópicos, mensajes, autenticación de usuarios y más. Esta API se integra perfectamente
con MySQL para el almacenamiento de datos y utiliza Swagger para una documentación clara y detallada de la API.

## Funcionalidades
* Gestión de Usuarios: Registro de nuevos usuarios y autenticación de usuarios existentes mediante 
tokens JWT (JSON Web Tokens).
* Gestión de Tópicos: Creación, actualización y cierre de tópicos de discusión.
* Gestión de Mensajes: Adición, eliminación y recuperación de mensajes dentro de los tópicos.
* Filtrado por Curso: Filtrado de tópicos basado en cursos asociados.
* Paginación: Uso de solicitudes paginables para una recuperación eficiente de datos.

## Demostración

A continuación se muestra cómo utilizar la API de Foro Hub mediante Swagger:

1. #### Registro de Usuario:
    Dirígete a `POST /usuarios/registro` y completa los campos de nombre, email y clave.
    
   <img src="/img/user_regitrar.jpg" alt="registrar usuario"/>

2. #### Verificación del Usuario Registrado:
    Confirma que el usuario ha sido registrado correctamente.

    <img src="/img/user_registrado.jpg" alt="usuario registrado"/>

3. #### Autenticación:
   Usa `POST /auth/login` para autenticarte y obtener un jwtToken.
   
   <img src="/img/user_autenticar.jpg" alt="autenticar usuario"/>
   <img src="/img/user_autenticado.jpg" alt="usuario autenticado"/>

4. #### Autorizar:
   Ve al icono de "Authorize" en la parte superior derecha.
   
   <img src="/img/authorize01.jpg" alt="autenticar usuario"/>

5. #### Pegar Token:
   Ingresa el token encriptado.

    <img src="/img/authorize02.jpg" alt="autenticar usuario"/>

6. #### Uso de la API:
   Con el usuario autenticado, podrás acceder a todas las funcionalidades 
de `Foro Hub`.

    <img src="/img/topicos.jpg" alt="autenticar usuario"/>

## Acceso
Puede acceder a la API de Foro Hub localmente siguiendo estos pasos:

1. Clonar el Repositorio:
```bash
git clone https://github.com/Fica-Millan/alura_ForoHub.git
```

2. Ejecutar la Aplicación:
* Abra el proyecto en IntelliJ IDEA.
* Configure la conexión segura de la base de datos MySQL
  en application.properties mediante el uso de variables de entorno:

```properties
spring.datasource.url=jdbc:mysql://${MYSQL_HOST}/${MYSQL_NAME}
spring.datasource.username=${MYSQL_USER}
spring.datasource.password=${MYSQL_PASS}
```
* Compila y ejecuta la aplicación.

3. Explorar la API:
* Acceda a Swagger UI en http://localhost:8080/swagger-ui/index.html#/ para la documentación de la API.
* Utilice herramientas como Insomnia para probar los endpoints e interactuar con la API.

## Tecnologías Utilizadas
* Java 17: Lenguaje de programación para lógica backend.
* Spring Boot 2.6.5: Marco de trabajo para construir y desplegar aplicaciones Java.
* Swagger 3.0: Herramienta de documentación y exploración de API.
* MySQL 8: Sistema de gestión de base de datos relacional para almacenamiento de datos.
* Insomnia 2024.1: Cliente API RESTful para probar endpoints.

## Código de Ejemplo
### Crear un nuevo tópico
```java
@PostMapping
public ResponseEntity<DatosRegistroTopico> registrarTopico(
        @Valid @RequestBody DatosRegistroTopico datosRegistroTopico,
        UriComponentsBuilder uriComponentsBuilder) {

    Topico topico = topicoService.registrarTopico(datosRegistroTopico);

    URI uri = uriComponentsBuilder.path("/topicos/{id}")
            .buildAndExpand(topico.getId())
            .toUri();

    return ResponseEntity.created(uri).body(datosRegistroTopico);
}
```
### Obtener la lista de tópicos
```java
@GetMapping
public ResponseEntity<PagedModel<EntityModel<DatosListadoTopico>>> listadoTopicos(
        @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.ASC) Pageable paginacion) {

    Page<DatosListadoTopico> topicosPage = topicoService.listarTopicos(paginacion);

    PagedModel<EntityModel<DatosListadoTopico>> pagedModel = topicoService.convertirAPagedModel(topicosPage,
            pagedResourcesAssembler, paginacion);

    return ResponseEntity.ok(pagedModel);
}
```
### Obtener un tópico por ID
```java
@GetMapping("/{id}")
public ResponseEntity<EntityModel<Topico>> buscarDetalleTopicoPorId(@PathVariable Long id) {
    Optional<Topico> optionalTopico = topicoService.buscarTopicoPorId(id);

    if (optionalTopico.isPresent()) {
        Topico topico = optionalTopico.get();
        return ResponseEntity.ok(EntityModel.of(topico));
    } else {
        return ResponseEntity.notFound().build();
    }
}
```
### Buscar tópico por curso
```java
@GetMapping("/buscar")
public ResponseEntity<PagedModel<EntityModel<DatosListadoTopico>>> buscarTopicosPorCurso(
    @RequestParam(name = "curso") String nombreCurso,
    @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.ASC) Pageable paginacion) {

    Page<DatosListadoTopico> datosListadoTopicoPage = topicoService.buscarTopicosPorCurso(nombreCurso, paginacion);

    PagedModel<EntityModel<DatosListadoTopico>> pagedModel = topicoService.convertirAPagedModel(datosListadoTopicoPage,
                pagedResourcesAssembler, paginacion);

    return ResponseEntity.ok(pagedModel);
}
```
### Agregar un nuevo mensaje a un tópico existente
```java
@PutMapping("/{id}")
public ResponseEntity <DatosListadoMensaje>actualizarTopico(@PathVariable Long id,
    @Valid @RequestBody DatosActualizarTopico datosActualizarTopico){

    topicoService.actualizarTopico(id, datosActualizarTopico);

    DatosListadoMensaje datosUltimoMensaje = topicoService.obtenerUltimoMensaje(id);

    return ResponseEntity.ok(datosUltimoMensaje);
}
```
### Eliminar un mensaje de un tópico
```java
@DeleteMapping("/{idTopico}/mensajes/{idMensaje}")
public ResponseEntity<String> eliminarMensaje(@PathVariable Long idTopico,
    @PathVariable Long idMensaje) {
    
    topicoService.eliminarMensaje(idTopico, idMensaje);
    return ResponseEntity.ok("Mensaje eliminado exitosamente");
}
```
### Cerrar un tópico
```java
@DeleteMapping("/{id}")
@Transactional
public ResponseEntity<String> cerrarTopico(@PathVariable Long id) {
        topicoService.cerrarTopico(id);
        return ResponseEntity.ok("Tópico cerrado exitosamente");
    }
```

## Contribución
Si deseas contribuir a este proyecto, por favor sigue los siguientes pasos:

1. Realiza un fork del repositorio.
2. Crea una nueva rama (`git checkout -b feature-nueva-funcionalidad`).
3. Realiza tus cambios y haz commit (`git commit -m 'Agrega nueva funcionalidad'`).
4. Empuja tus cambios a la rama (`git push origin feature-nueva-funcionalidad`).
5. Abre un Pull Request.

Por favor, asegúrate de seguir las buenas prácticas de contribución y 
respeta el código de conducta del proyecto. Esto incluye escribir código limpio y bien documentado, realizar pruebas exhaustivas antes de enviar tus cambios, y mantener un ambiente respetuoso y colaborativo en todas las interacciones.

## Agradecimientos
Agradezco al **Programa ONE** de [Alura Latam](https://www.linkedin.com/company/alura-latam/) y
[Oracle](https://www.linkedin.com/company/oracle/) por proveer el material y el contexto para 
desarrollar este proyecto.

## Autor
Este proyecto fue creado por [Fica](https://github.com/Fica-Millan).

¡Siéntete libre de contactarme si tienes alguna pregunta o sugerencia!

[LinkedIn](https://www.linkedin.com/in/yesica-fica-millan/)


