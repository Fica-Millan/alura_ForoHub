package com.forohub.foro_api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.forohub.foro_api.dto.DatosNuevoMensaje;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "mensajes")
@Entity(name = "Mensaje")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenido;
    private LocalDateTime fecha;
    private String autor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topico_id")
    @JsonBackReference
    private Topico topico;

    public Mensaje(String contenido, String autor) {
        this.contenido = contenido;
        this.fecha = LocalDateTime.now();
        this.autor = autor;
    }

    public Mensaje(DatosNuevoMensaje datosNuevoMensaje) {
    }
}
