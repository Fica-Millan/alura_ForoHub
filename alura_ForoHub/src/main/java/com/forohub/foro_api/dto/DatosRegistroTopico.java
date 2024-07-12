package com.forohub.foro_api.dto;

import com.forohub.foro_api.model.Curso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosRegistroTopico(
        @NotBlank(message = "El título es obligatorio.")
        String titulo,
        @NotBlank(message = "El mensaje es obligatorio.")
        String mensaje,
        @NotBlank(message = "El autor es obligatorio.")
        String autor,
        @NotNull(message = "El curso es obligatorio.")
        Curso curso) {
}
