package com.forohub.foro_api.controller;

import com.forohub.foro_api.dto.DatosNuevoUsuario;
import com.forohub.foro_api.model.Usuario;
import com.forohub.foro_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios, incluyendo registro y consulta.")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Registra un nuevo usuario.
     *
     * @param datosNuevoUsuario Datos del nuevo usuario a registrar.
     * @return ResponseEntity<Usuario> con los detalles del usuario registrado.
     */
    @PostMapping("/registro")
    @Operation(summary = "Registrar un nuevo usuario",
            description = "Registra un nuevo usuario en el sistema.")
    public ResponseEntity<Usuario> registrarUsuario(
            @Parameter(description = "Datos del nuevo usuario a registrar", required = true)
            @Valid @RequestBody DatosNuevoUsuario datosNuevoUsuario) {
        Usuario usuarioCreado = usuarioService.registrarNuevoUsuario(datosNuevoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }
}
