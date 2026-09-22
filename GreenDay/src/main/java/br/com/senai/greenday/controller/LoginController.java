package br.com.senai.greenday.controller;

import br.com.senai.greenday.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String erro, Model model) {
        if ("google".equals(erro)) {
            model.addAttribute("erro", "Não foi possível entrar com o Google. Verifique a configuração do Google OAuth.");
        } else if ("desativado".equals(erro)) {
            model.addAttribute("erro", "Esta conta está desativada. Procure o administrador.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(
            @RequestParam String email,
            @RequestParam String senha,
            jakarta.servlet.http.HttpServletResponse response,
            Model model) {

        try {

            // Autentica o usuário usando Spring Security
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            senha
                    )
            );

            // Usuário autenticado
            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();

            // Gera o JWT
            String token = jwtService.gerarToken(userDetails);

            // Coloca o JWT em um cookie
            ResponseCookie cookie = ResponseCookie
                    .from("JWT", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(60 * 60)
                    .sameSite("Lax")
                    .build();

            response.addHeader(
                    "Set-Cookie",
                    cookie.toString()
            );

            // Vai para a página inicial
            return "redirect:/dashboard";

        } catch (AuthenticationException e) {

            model.addAttribute(
                    "erro",
                    "E-mail ou senha incorretos"
            );

            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(
            jakarta.servlet.http.HttpServletResponse response) {

        // Apaga o cookie JWT
        ResponseCookie cookie = ResponseCookie
                .from("JWT", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
        );

        return "redirect:/";
    }
}