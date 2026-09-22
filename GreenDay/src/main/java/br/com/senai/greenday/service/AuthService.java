package br.com.senai.greenday.service;

import br.com.senai.greenday.dto.LoginDTO;
import br.com.senai.greenday.model.Usuario;
import br.com.senai.greenday.repository.UsuarioRepository;
import br.com.senai.greenday.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public String autenticar(LoginDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha()));
        Usuario usuario = usuarioService.buscarPorEmail(dto.getEmail());
        return jwtService.gerarToken(usuario);
    }

    public String autenticarPorTelefone(String telefone, String senha) {
        Usuario usuario = usuarioRepository.findByTelefone(telefone)
                .orElseThrow(() -> new IllegalArgumentException("Telefone não cadastrado."));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuario.getEmail(), senha));

        return jwtService.gerarToken(usuario);
    }
}
