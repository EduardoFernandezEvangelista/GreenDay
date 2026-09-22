package br.com.senai.greenday.security;

import br.com.senai.greenday.model.Role;
import br.com.senai.greenday.model.Usuario;
import br.com.senai.greenday.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User google = (OAuth2User) authentication.getPrincipal();
        String email = google.getAttribute("email");
        String nome = google.getAttribute("name");

        if (email == null || email.isBlank()) {
            response.sendRedirect("/login?erro=google");
            return;
        }

        Usuario usuario = usuarioRepository.findByEmail(email).orElseGet(() ->
                usuarioRepository.save(Usuario.builder()
                        .nome(nome != null ? nome : email)
                        .email(email)
                        .senha("GOOGLE_LOGIN")
                        .role(Role.PRODUTOR)
                        .ativo(true)
                        .build()));

        if (!usuario.isEnabled()) {
            response.sendRedirect("/login?erro=desativado");
            return;
        }

        String token = jwtService.gerarToken(usuario);
        ResponseCookie cookie = ResponseCookie.from("JWT", token)
                .httpOnly(true).secure(false).path("/").maxAge(60 * 60)
                .sameSite("Lax").build();
        response.addHeader("Set-Cookie", cookie.toString());
        response.sendRedirect("/dashboard");
    }
}
