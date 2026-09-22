package br.com.senai.greenday;

import com.greenday.dto.CadastroDTO;
import com.greenday.model.Role;
import com.greenday.model.Usuario;
import com.greenday.repository.UsuarioRepository;
import com.greenday.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private CadastroDTO cadastroDTO;

    @BeforeEach
    void setUp() {
        cadastroDTO = new CadastroDTO("Maria Silva", "maria@greenday.com", "senha123", "11999999999");
    }

    @Test
    void deveCadastrarNovoUsuarioComSucesso() {
        when(usuarioRepository.existsByEmail(cadastroDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(cadastroDTO.getSenha())).thenReturn("senha-criptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.cadastrar(cadastroDTO);

        assertEquals("Maria Silva", resultado.getNome());
        assertEquals(Role.PRODUTOR, resultado.getRole());
        assertEquals("senha-criptografada", resultado.getSenha());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void naoDeveCadastrarUsuarioComEmailDuplicado() {
        when(usuarioRepository.existsByEmail(cadastroDTO.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.cadastrar(cadastroDTO));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        Usuario usuario = Usuario.builder().id(1L).email("maria@greenday.com").build();
        when(usuarioRepository.findByEmail("maria@greenday.com")).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorEmail("maria@greenday.com");

        assertEquals(1L, resultado.getId());
    }
}
