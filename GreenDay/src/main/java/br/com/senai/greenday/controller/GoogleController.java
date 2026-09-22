package br.com.senai.greenday.controller;

import br.com.senai.greenday.model.Role;
import br.com.senai.greenday.model.Usuario;
import br.com.senai.greenday.repository.UsuarioRepository;
import br.com.senai.greenday.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseCookie;

@Controller
@RequiredArgsConstructor
public class GoogleController {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @GetMapping("/login/google")
    public String googleLogin() {
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/login/google/sucesso")
    public String sucesso(Authentication authentication, HttpServletResponse response) {
        OAuth2User google = (OAuth2User) authentication.getPrincipal();
        String email = google.getAttribute("email");
        String nome = google.getAttribute("name");
        if (email == null || email.isBlank()) return "redirect:/login?erro=google";

        Usuario usuario = usuarioRepository.findByEmail(email).orElseGet(() -> {
            Usuario novo = Usuario.builder().nome(nome != null ? nome : email).email(email)
                    .senha("GOOGLE_LOGIN").role(Role.PRODUTOR).ativo(true).build();
            return usuarioRepository.save(novo);
        });
        if (!usuario.isEnabled()) return "redirect:/login?erro=desativado";

        String token = jwtService.gerarToken(usuario);
        ResponseCookie cookie = ResponseCookie.from("JWT", token).httpOnly(true).secure(false)
                .path("/").maxAge(60 * 60).sameSite("Lax").build();
        response.addHeader("Set-Cookie", cookie.toString());
        return "redirect:/dashboard";
    }
}
