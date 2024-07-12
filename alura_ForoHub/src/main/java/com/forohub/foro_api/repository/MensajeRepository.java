package com.forohub.foro_api.repository;

import com.forohub.foro_api.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    void deleteById(Long id);
}
