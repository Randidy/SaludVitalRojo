package com.saludvital.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saludvital.model.Usuario;
import com.saludvital.repository.UsuarioRepository;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔹 Registrar un usuario (encriptando la contraseña) - siempre admin
    public Usuario registrarUsuario(Usuario usuario) {
        usuario.setRoles("ADMIN"); // fijo admin
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    // 🔹 Listar solo administradores
    public List<Usuario> listarAdmins() {
        return usuarioRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRoles()))
                .collect(Collectors.toList());
    }

    // 🔹 Buscar usuario por ID, lanza excepción si no existe
    public Usuario obtenerPorId(Long id) throws Exception {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            return usuario.get();
        } else {
            throw new Exception("Usuario no encontrado");
        }
    }

    // 🔹 Buscar usuario por ID, devuelve null si no existe
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // 🔹 Actualizar usuario completo, con contraseña opcional (solo admin)
    public Usuario actualizarUsuario(Long id, String usuarioNombre, String password) throws Exception {
        Usuario usuarioExistente = obtenerPorId(id);
        usuarioExistente.setUsuario(usuarioNombre);
        usuarioExistente.setRoles("ADMIN"); // fijo admin

        if (password != null && !password.isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(password));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    // 🔹 Eliminar usuario solo si es admin
    public void eliminarUsuario(Long id) throws Exception {
        Usuario usuario = obtenerPorId(id);
        if (!"ADMIN".equals(usuario.getRoles())) {
            throw new Exception("No se puede eliminar un usuario que no sea ADMIN");
        }
        usuarioRepository.deleteById(id);
    }

    // 🔹 Crear un usuario ADMIN por defecto al iniciar la app
    @PostConstruct
    public void crearAdminPorDefecto() {
        String usuarioAdmin = "admin";
        if (!usuarioRepository.existsByUsuario(usuarioAdmin)) {
            Usuario admin = new Usuario();
            admin.setUsuario(usuarioAdmin);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles("ADMIN");
            usuarioRepository.save(admin);
            System.out.println("🟢 Usuario ADMIN creado con éxito.");
        } else {
            System.out.println("ℹ️ Usuario ADMIN ya existe.");
        }
    }
}
