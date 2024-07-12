package com.forohub.foro_api.service;

import com.forohub.foro_api.dto.DatosNuevoUsuario;
import com.forohub.foro_api.model.Usuario;
import com.forohub.foro_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Usuario registrarNuevoUsuario(DatosNuevoUsuario datosNuevoUsuario) {
        // Crear una instancia de Usuario con los datos del DTO
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(datosNuevoUsuario.nombre());
        nuevoUsuario.setEmail(datosNuevoUsuario.email());
        // Encriptar la contraseña antes de guardarla en la base de datos
        nuevoUsuario.setClave(passwordEncoder.encode(datosNuevoUsuario.clave()));

        // Guardar el nuevo usuario en la base de datos
        return usuarioRepository.save(nuevoUsuario);
    }
}
