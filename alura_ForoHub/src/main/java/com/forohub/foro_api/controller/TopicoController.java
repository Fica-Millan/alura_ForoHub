package com.forohub.foro_api.controller;

import com.forohub.foro_api.dto.*;
import com.forohub.foro_api.model.*;
import com.forohub.foro_api.service.TopicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Tópicos", description = "Operaciones relacionadas con los tópicos")
public class TopicoController {

    @Autowired
    private TopicoService topicoService;

    @Autowired
    private PagedResourcesAssembler<DatosListadoTopico> pagedResourcesAssembler;


    /**
     * Registra un nuevo tópico.
     *
     * @param datosRegistroTopico  Datos del nuevo tópico a registrar.
     * @param uriComponentsBuilder Constructor de URI para crear la ubicación del nuevo recurso.
     * @return ResponseEntity con la URI del nuevo recurso y los datos del tópico registrado.
     */
    @PostMapping
    @Operation(summary = "Crea un nuevo tópico", description = "Permite la creación de un nuevo tópico")
    public ResponseEntity<DatosRegistroTopico> registrarTopico(
            @Valid @RequestBody DatosRegistroTopico datosRegistroTopico,
            UriComponentsBuilder uriComponentsBuilder) {

        Topico topico = topicoService.registrarTopico(datosRegistroTopico);

        URI uri = uriComponentsBuilder.path("/topicos/{id}")
                .buildAndExpand(topico.getId())
                .toUri();

        return ResponseEntity.created(uri).body(datosRegistroTopico);
    }


    /**
     * Lista los tópicos existentes.
     *
     * @param paginacion Información de paginación y ordenamiento.
     * @return ResponseEntity con el modelo paginado de los tópicos listados.
     */
    @GetMapping
    @Operation(summary = "Obtiene la lista de tópicos", description = "Devuelve una lista de todos los tópicos existentes")
    public ResponseEntity<PagedModel<EntityModel<DatosListadoTopico>>> listadoTopicos(
            @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.ASC) Pageable paginacion) {

        // Obtener la página de DTO DatosListadoTopico desde el servicio
        Page<DatosListadoTopico> topicosPage = topicoService.listarTopicos(paginacion);

        // Convertir la página de DTO DatosListadoTopico a un modelo paginado de recursos usando el servicio
        PagedModel<EntityModel<DatosListadoTopico>> pagedModel = topicoService.convertirAPagedModel(topicosPage,
                pagedResourcesAssembler, paginacion);

        return ResponseEntity.ok(pagedModel);
    }


    /**
     * Busca los tópicos existentes referentes a un curso en específico.
     *
     * @param nombreCurso Nombre del curso.
     * @param paginacion  Información de paginación y ordenamiento.
     * @return ResponseEntity con el modelo paginado de los tópicos filtrados por curso.
     */
    @GetMapping("/buscar")
    @Operation(summary = "Buscar tópicos por curso",
            description = "Busca los tópicos existentes referentes a un curso en específico.")
    public ResponseEntity<PagedModel<EntityModel<DatosListadoTopico>>> buscarTopicosPorCurso(
            @Parameter(description = "Nombre del curso", required = true)
            @RequestParam(name = "curso") String nombreCurso,
            @Parameter(description = "Información de paginación y ordenamiento")
            @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.ASC) Pageable paginacion) {

        // Mapear la página de tópicos a una página de datos listados de tópicos
        Page<DatosListadoTopico> datosListadoTopicoPage = topicoService.buscarTopicosPorCurso(nombreCurso, paginacion);

        // Convertir la página de DTO DatosListadoTopico a un modelo paginado de recursos usando el servicio
        PagedModel<EntityModel<DatosListadoTopico>> pagedModel = topicoService.convertirAPagedModel(datosListadoTopicoPage,
                pagedResourcesAssembler, paginacion);

        // Convertir la página de datos listados de tópicos a un modelo paginado de EntityModel
        return ResponseEntity.ok(pagedModel);
    }


     /**
     * Busca un tópico en particular según su ID.
     *
     * @param id Identificador del tópico.
     * @return Detalle del tópico si se encuentra, de lo contrario, 404 Not Found.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un tópico por ID", description = "Devuelve un tópico específico basado en su ID")
    public ResponseEntity<EntityModel<Topico>> buscarDetalleTopicoPorId(
            @Parameter(description = "ID del tópico a obtener", required = true) @PathVariable Long id) {
        Optional<Topico> optionalTopico = topicoService.buscarTopicoPorId(id);

        // Si el Optional de Topico tiene un valor presente, obtén el objeto Topico y devuelve una respuesta OK con sus datos encapsulados en EntityModel.
        if (optionalTopico.isPresent()) {
            Topico topico = optionalTopico.get();
            return ResponseEntity.ok(EntityModel.of(topico));
        } else {
            // Si no se encuentra un Topico con el ID proporcionado, devuelve una respuesta HTTP 404 (Not Found).
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Actualiza un tópico existente con nuevos datos.
     *
     * @param id Identificador del tópico a actualizar.
     * @param datosActualizarTopico Datos nuevos para actualizar el tópico.
     * @return ResponseEntity<DatosListadoMensaje> Datos del último mensaje actualizado.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un tópico existente", description = "Permite la actualización de un tópico basado en su ID")
    public ResponseEntity <DatosListadoMensaje>actualizarTopico(
            @Parameter(description = "ID del tópico a actualizar", required = true) @PathVariable Long id,
            @Valid @RequestBody DatosActualizarTopico datosActualizarTopico){

        // Llamar al servicio para actualizar el tópico
        topicoService.actualizarTopico(id, datosActualizarTopico);

        // Obtener los datos del último mensaje
        DatosListadoMensaje datosUltimoMensaje = topicoService.obtenerUltimoMensaje(id);

        // Devolver respuesta con los datos del último mensaje
        return ResponseEntity.ok(datosUltimoMensaje);
    }


    /**
     * Agrega un nuevo mensaje a un tópico existente.
     *
     * @param id Identificador del tópico al cual se añadirá el mensaje.
     * @param datosNuevoMensaje Datos del nuevo mensaje a agregar.
     * @return ResponseEntity<DatosListadoMensaje> con los detalles del mensaje añadido.
     */
    @PostMapping("/{id}/mensajes")
    @Operation(summary = "Agregar un nuevo mensaje a un tópico",
            description = "Agrega un nuevo mensaje a un tópico existente.")
    public ResponseEntity<DatosListadoMensaje> agregarMensaje(
            @Parameter(description = "Identificador del tópico al cual se añadirá el mensaje", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos del nuevo mensaje a agregar", required = true)
            @Valid @RequestBody DatosNuevoMensaje datosNuevoMensaje) {
        DatosListadoMensaje nuevoMensaje = topicoService.agregarMensaje(id, datosNuevoMensaje);
        return ResponseEntity.ok().body(nuevoMensaje);
    }


    /**
     * Marca un tópico como "CERRADO".
     *
     * @param id Identificador del tópico a cerrar.
     * @return ResponseEntity<String> con un mensaje de confirmación.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Cerrar un tópico", description = "Permite la eliminación lógica de un tópico basado en su ID")
    @Transactional
    public ResponseEntity<String> cerrarTopico(
            @Parameter(description = "ID del tópico a eliminar", required = true) @PathVariable Long id) {
        topicoService.cerrarTopico(id);
        return ResponseEntity.ok("Tópico cerrado exitosamente");
    }


    /**
     * Elimina definitivamente un mensaje de un tópico.
     *
     * @param idTopico Identificador del tópico que contiene el mensaje.
     * @param idMensaje Identificador del mensaje a eliminar.
     * @return ResponseEntity con un mensaje de confirmación.
     */
    @DeleteMapping("/{idTopico}/mensajes/{idMensaje}")
    @Operation(summary = "Elimina un mensaje de un tópico",
            description = "Elimina definitivamente un mensaje de un tópico basado en los identificadores del tópico y del mensaje.")
    public ResponseEntity<String> eliminarMensaje(
            @Parameter(description = "Identificador del tópico que contiene el mensaje", required = true)
            @PathVariable Long idTopico,
            @Parameter(description = "Identificador del mensaje a eliminar", required = true)
            @PathVariable Long idMensaje) {
        topicoService.eliminarMensaje(idTopico, idMensaje);
        return ResponseEntity.ok("Mensaje eliminado exitosamente");
    }
}
