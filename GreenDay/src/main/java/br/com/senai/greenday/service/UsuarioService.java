package br.com.senai.greenday.service;

import br.com.senai.greenday.dto.CadastroDTO;
import br.com.senai.greenday.exception.ResourceNotFoundException;
import br.com.senai.greenday.model.Role;
import br.com.senai.greenday.model.Usuario;
import br.com.senai.greenday.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario cadastrar(CadastroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ja existe um usuario com esse email");
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .telefone(dto.getTelefone())
                .role(Role.PRODUTOR)
                .build();

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado: " + id));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado: " + email));
    }

    public Usuario atualizar(Long id, CadastroDTO dto) {
        Usuario usuario = buscarPorId(id);
        usuario.setNome(dto.getNome());
        usuario.setTelefone(dto.getTelefone());
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarAdmin(Long id, String nome, String telefone, Role role) {
        Usuario usuario = buscarPorId(id);

        usuario.setNome(nome);
        usuario.setTelefone(telefone);
        usuario.setRole(role);

        return usuarioRepository.save(usuario);
    }

    public void desativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public void ativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }
}
