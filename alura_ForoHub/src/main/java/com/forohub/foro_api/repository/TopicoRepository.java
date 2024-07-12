package com.forohub.foro_api.repository;

import com.forohub.foro_api.model.Curso;
import com.forohub.foro_api.model.Topico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

    // Verificar si existe un tópico con el mismo título y mensaje
    boolean existsByTituloAndMensajes_contenido(String titulo, String mensaje);

    // Consulta personalizada para excluir tópicos cerrados
    @Query("SELECT t FROM Topico t WHERE t.status <> 'CERRADO'")
    Page<Topico> findAllActive(Pageable pageable);

    // Método para encontrar tópicos por curso excluyendo los cerrados
    @Query("SELECT t FROM Topico t WHERE t.curso = :curso AND t.status <> 'CERRADO'")
    Page<Topico> findByCursoAndStatusNotClosed(@Param("curso") Curso curso, Pageable pageable);
}
