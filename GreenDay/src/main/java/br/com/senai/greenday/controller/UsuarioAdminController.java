package br.com.senai.greenday.controller;

import br.com.senai.greenday.model.Role;
import br.com.senai.greenday.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        model.addAttribute("roles", Role.values());
        return "usuario-form";
    }

    @PostMapping("/usuarios/salvar")
    public String salvar(
            @RequestParam Long id,
            @RequestParam String nome,
            @RequestParam(required = false) String telefone,
            @RequestParam Role role,
            RedirectAttributes redirectAttributes) {

        usuarioService.atualizarAdmin(id, nome, telefone, role);
        redirectAttributes.addFlashAttribute("sucesso", "Usuário atualizado com sucesso!");
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/desativar/{id}")
    public String desativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioService.desativar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Usuário desativado com sucesso!");
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/ativar/{id}")
    public String ativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioService.ativar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Usuário ativado com sucesso!");
        return "redirect:/usuarios";
    }
}
