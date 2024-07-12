package com.forohub.foro_api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.forohub.foro_api.dto.DatosActualizarTopico;
import com.forohub.foro_api.dto.DatosRegistroTopico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "topicos")
@Entity(name = "Topico")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;

    @OneToMany(mappedBy = "topico", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Mensaje> mensajes = new ArrayList<>();

    private LocalDateTime fecha;
    private String status;
    private String autor;

    @Enumerated(EnumType.STRING)
    private Curso curso;

    public Topico(DatosRegistroTopico datosRegistroTopico) {
        this.titulo = datosRegistroTopico.titulo();
        this.fecha = LocalDateTime.now();
        this.status = "ABIERTO";
        this.autor = datosRegistroTopico.autor();
        this.curso = datosRegistroTopico.curso();
        this.mensajes = new ArrayList<>();
        Mensaje mensaje = new Mensaje(datosRegistroTopico.mensaje(), this.autor);
        this.agregarMensaje(mensaje);
    }

    public void agregarMensaje(Mensaje mensaje) {
        mensajes.add(mensaje);
        mensaje.setTopico(this);
    }

    public void actualizarTopico(DatosActualizarTopico datosActualizarTopico) {
        if (datosActualizarTopico.mensaje() != null) {
            Mensaje mensaje = new Mensaje(datosActualizarTopico.mensaje(), datosActualizarTopico.autor());
            this.agregarMensaje(mensaje);
        }
        this.fecha = LocalDateTime.now();
        this.status = "ACTUALIZADO";
    }

    public void cerrarTopico (){
        this.status = "CERRADO";
    }

}
