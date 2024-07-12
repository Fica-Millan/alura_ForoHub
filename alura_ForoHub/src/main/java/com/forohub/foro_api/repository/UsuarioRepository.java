package com.forohub.foro_api.repository;

import com.forohub.foro_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository <Usuario, Long>{
    UserDetails findByEmail(String username);
}
