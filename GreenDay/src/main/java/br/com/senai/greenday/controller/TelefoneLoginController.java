package br.com.senai.greenday.controller;

import br.com.senai.greenday.security.JwtService;
import br.com.senai.greenday.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class TelefoneLoginController {

    private final AuthService authService;
    private final JwtService jwtService;

    @GetMapping("/login/telefone")
    public String pagina() {
        return "login-telefone";
    }

    @PostMapping("/login/telefone")
    public String entrar(@RequestParam String telefone,
                         @RequestParam String senha,
                         jakarta.servlet.http.HttpServletResponse response,
                         Model model) {
        try {
            String token = authService.autenticarPorTelefone(telefone.trim(), senha);
            ResponseCookie cookie = ResponseCookie.from("JWT", token)
                    .httpOnly(true).secure(false).path("/").maxAge(60 * 60)
                    .sameSite("Lax").build();
            response.addHeader("Set-Cookie", cookie.toString());
            return "redirect:/dashboard";
        } catch (AuthenticationException | IllegalArgumentException e) {
            model.addAttribute("erro", "Telefone ou senha incorretos.");
            return "login-telefone";
        }
    }
}
