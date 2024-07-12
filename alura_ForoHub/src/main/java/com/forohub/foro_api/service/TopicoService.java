package com.forohub.foro_api.service;

import com.forohub.foro_api.controller.TopicoController;
import com.forohub.foro_api.dto.*;
import com.forohub.foro_api.model.Curso;
import com.forohub.foro_api.model.Mensaje;
import com.forohub.foro_api.model.Topico;
import com.forohub.foro_api.repository.TopicoRepository;
import com.forohub.foro_api.repository.MensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TopicoService {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private MensajeRepository mensajeRepository;


    /**
     * Registra un nuevo tópico en el repositorio.
     *
     * Este método verifica si ya existe un tópico con el mismo título y mensaje.
     * Si ya existe, lanza una excepción indicando un error de solicitud.
     * Si no existe, guarda el nuevo tópico en el repositorio.
     *
     * @param datosRegistroTopico Datos del nuevo tópico a registrar.
     * @return El tópico registrado.
     * @throws ResponseStatusException si el tópico ya existe.
     */
    public Topico registrarTopico(DatosRegistroTopico datosRegistroTopico) {
        // Verificar si ya existe un tópico con el mismo título y mensaje
        if (topicoRepository.existsByTituloAndMensajes_contenido(datosRegistroTopico.titulo(), datosRegistroTopico.mensaje())) {
            // Lanzar una excepción indicando que el tópico ya existe
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tópico ya existe.");
        }
        // Si no existe, proceder a guardar el nuevo tópico
        Topico nuevoTopico = new Topico(datosRegistroTopico);
        return topicoRepository.save(nuevoTopico);
    }


    /**
     * Lista todos los tópicos activos del repositorio con paginación.
     *
     * Este método obtiene todos los tópicos activos del repositorio y los
     * mapea a objetos de DTO de listado de tópicos para ser retornados en una página.
     *
     * @param paginacion Parámetros de paginación y ordenamiento.
     * @return Una página de objetos de DTO de listado de tópicos.
     */
    public Page<DatosListadoTopico> listarTopicos(Pageable paginacion) {
        return topicoRepository.findAllActive(paginacion).map(DatosListadoTopico::new);
    }


    /**
     * Convierte una página de entidades Topico a un modelo paginado de recursos de DTO DatosListadoTopico.
     *
     * @param topicosPage La página de entidades Topico.
     * @param pagedResourcesAssembler El ensamblador de recursos paginados.
     * @param paginacion Los parámetros de paginación.
     * @return El modelo paginado de recursos.
     */
    public PagedModel<EntityModel<DatosListadoTopico>> convertirAPagedModel(Page<DatosListadoTopico> topicosPage,
                                                                            PagedResourcesAssembler<DatosListadoTopico> pagedResourcesAssembler,
                                                                            Pageable paginacion) {
        return pagedResourcesAssembler.toModel(topicosPage,
                topico -> EntityModel.of(topico,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TopicoController.class)
                                .listadoTopicos(paginacion)).withSelfRel()));
    }


    /**
     * Busca y lista tópicos activos por curso con paginación.
     *
     * Este método recibe el nombre de un curso como String, lo convierte a un enum
     * Curso, y luego busca todos los tópicos asociados a ese curso que no estén cerrados.
     * Los resultados se mapean a objetos DTO de listado de tópicos y se retornan en una página.
     *
     * @param nombreCurso Nombre del curso a buscar.
     * @param paginacion Parámetros de paginación y ordenamiento.
     * @return Una página de objetos de DTO de listado de tópicos asociados al curso.
     * @throws ResponseStatusException Si el nombre del curso es inválido.
     */
    public Page<DatosListadoTopico> buscarTopicosPorCurso(String nombreCurso, Pageable paginacion) {
        Curso curso;
        try {
            // Convertir el String a Curso enum
            curso = Curso.valueOf(nombreCurso.toUpperCase()); // Convertir el String a Curso enum
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso inválido");
        }
        return topicoRepository.findByCursoAndStatusNotClosed(curso, paginacion).map(DatosListadoTopico::new);
    }


    /**
     * Busca un tópico por su ID.
     *
     * Este método recibe un identificador único de un tópico y lo busca en el repositorio.
     * Si el tópico existe, retorna un Optional que contiene el tópico; de lo contrario,
     * retorna un Optional vacío.
     *
     * @param id Identificador único del tópico.
     * @return Un Optional que contiene el tópico si se encuentra, de lo contrario, un Optional vacío.
     */
    public Optional<Topico> buscarTopicoPorId(Long id) {

        return topicoRepository.findById(id);
    }


    /**
     * Actualiza un tópico existente con nuevos datos.
     *
     * Este método busca un tópico por su ID en el repositorio. Si el tópico existe, se actualizan
     * sus datos con la información proporcionada. Si el tópico no se encuentra, lanza una excepción
     * con un código de estado 404 Not Found.
     *
     * @param id Identificador único del tópico a actualizar.
     * @param datosActualizarTopico Objeto que contiene los nuevos datos para actualizar el tópico.
     * @throws ResponseStatusException si el tópico no se encuentra.
     */
    @Transactional
    public void actualizarTopico(Long id, DatosActualizarTopico datosActualizarTopico) {
        // Buscar el tópico por su ID en el repositorio
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // Actualizar el tópico con los nuevos datos
        topico.actualizarTopico(datosActualizarTopico);

        // Guardar los cambios en el repositorio
        topicoRepository.save(topico);
    }


    /**
     * Obtiene los datos del último mensaje agregado a un tópico.
     *
     * @param id Identificador del tópico.
     * @return DatosListadoMensaje con la información del último mensaje.
     * @throws ResponseStatusException si el tópico no se encuentra.
     */
    public DatosListadoMensaje obtenerUltimoMensaje(Long id) {
        // Buscar el tópico por su ID en el repositorio
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // Obtener el último mensaje agregado al tópico (asumiendo que está en la última posición)
        List<Mensaje> mensajes = topico.getMensajes();
        if (!mensajes.isEmpty()) {
            Mensaje ultimoMensaje = mensajes.get(mensajes.size() - 1);
            return new DatosListadoMensaje(
                    ultimoMensaje.getId(),
                    ultimoMensaje.getContenido(),
                    ultimoMensaje.getFecha(),
                    ultimoMensaje.getAutor()
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay mensajes en el tópico");
        }
    }


    /**
     * Agrega un nuevo mensaje a un tópico existente.
     *
     * Este método busca un tópico por su ID en el repositorio. Si el tópico existe, se crea un nuevo mensaje
     * con la información proporcionada y se añade al tópico. Luego, se guarda el mensaje y el tópico en los
     * repositorios correspondientes. Si el tópico no se encuentra, lanza una excepción con un código de estado
     * 404 Not Found.
     *
     * @param id Identificador único del tópico al cual se añadirá el mensaje.
     * @param datosNuevoMensaje Objeto que contiene los datos del nuevo mensaje a agregar.
     * @return DatosListadoMensaje con los detalles del mensaje añadido.
     */
    @Transactional
    public DatosListadoMensaje agregarMensaje(Long id, DatosNuevoMensaje datosNuevoMensaje) {
        // Buscar el tópico por su ID en el repositorio
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // Crear un nuevo mensaje con los datos proporcionados
        Mensaje nuevoMensaje = new Mensaje(datosNuevoMensaje);

        // Añadir el mensaje al tópico
        topico.agregarMensaje(nuevoMensaje);

        // Guardar el mensaje y el tópico en los repositorios
        topicoRepository.save(topico);
        return new DatosListadoMensaje(nuevoMensaje);
    }


    /**
     * Marca un tópico como "CERRADO".
     *
     * Este método busca un tópico por su ID en el repositorio. Si el tópico existe, se marca como "CERRADO"
     * y se guarda el estado actualizado en el repositorio. Si el tópico no se encuentra, lanza una excepción
     * con un código de estado 404 Not Found.
     *
     * @param id Identificador único del tópico a cerrar.
     */
    @Transactional
    public void cerrarTopico(Long id) {
        // Buscar el tópico por su ID
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // Marcar el tópico como "CERRADO"
        topico.cerrarTopico();

        // Guardar el estado actualizado del tópico en el repositorio
        topicoRepository.save(topico);
    }


    /**
     * Elimina definitivamente un mensaje de un tópico.
     *
     * Este método busca y elimina de manera permanente un mensaje específico asociado a un tópico.
     * Primero busca el tópico por su ID y luego busca el mensaje dentro de los mensajes del tópico.
     * Una vez encontrado, elimina el mensaje tanto de la lista de mensajes del tópico como de la
     * base de datos.
     *
     * @param idTopico Identificador del tópico que contiene el mensaje.
     * @param idMensaje Identificador del mensaje a eliminar.
     * @throws ResponseStatusException Si no se encuentra el tópico o el mensaje correspondiente.
     */
    @Transactional
    public void eliminarMensaje(Long idTopico, Long idMensaje) {
        // Buscar el tópico por su ID en el repositorio
        Topico topico = topicoRepository.findById(idTopico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // Buscar el mensaje dentro de los mensajes del tópico
        Mensaje mensaje = topico.getMensajes().stream()
                .filter(m -> m.getId().equals(idMensaje))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mensaje no encontrado"));

        // Remover el mensaje de la lista de mensajes del tópico
        topico.getMensajes().remove(mensaje);

        // Guardar los cambios en el repositorio del tópico
        topicoRepository.save(topico);

        // Eliminar el mensaje de la base de datos
        mensajeRepository.deleteById(idMensaje);
    }
}
