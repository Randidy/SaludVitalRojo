package com.saludvital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.saludvital.model.Usuario;
import com.saludvital.service.UsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/inicio")
    public String inicioAdmin() {
        return "inicio_admin";
    }

    // 🔹 Mostrar formulario de nuevo admin
    @GetMapping("/nuevo-usuario")
    public String mostrarFormularioRegistro() {
        return "registro_usuario_admin";
    }

    // 🔹 Listado solo admins
    @GetMapping("/listado")
    public String listadoUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarAdmins());
        return "listado_usuarios_admin";
    }

    // 🔹 Registrar admin
    @PostMapping("/registrar")
    public String registrarUsuarioDesdeAdmin(@RequestParam String usuario,
                                             @RequestParam String password,
                                             RedirectAttributes attr) {
        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsuario(usuario);
            nuevoUsuario.setPassword(password);
            usuarioService.registrarUsuario(nuevoUsuario);

            attr.addFlashAttribute("mensaje", "Administrador registrado correctamente.");
        } catch (Exception e) {
            attr.addFlashAttribute("error", "Error al registrar administrador.");
        }
        return "redirect:/admin/nuevo-usuario";
    }

    // 🔹 Mostrar formulario edición admin
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, RedirectAttributes attr) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null || !"ADMIN".equals(usuario.getRoles())) {
            attr.addFlashAttribute("error", "Administrador no encontrado.");
            return "redirect:/admin/listado";
        }
        model.addAttribute("usuario", usuario);
        return "editar_usuario_admin";
    }

    // 🔹 Editar admin
    @PostMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id,
                                @RequestParam String usuario,
                                @RequestParam(required = false) String password,
                                RedirectAttributes attr) {
        try {
            usuarioService.actualizarUsuario(id, usuario, password);
            attr.addFlashAttribute("mensaje", "Administrador actualizado correctamente.");
        } catch (Exception e) {
            attr.addFlashAttribute("error", "Error al actualizar administrador.");
        }
        return "redirect:/admin/listado";
    }

    // 🔹 Eliminar admin
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes attr) {
        try {
            usuarioService.eliminarUsuario(id);
            attr.addFlashAttribute("mensaje", "Administrador eliminado correctamente.");
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/listado";
    }
}
