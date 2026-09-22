package br.com.senai.greenday.dto;

import br.com.senai.greenday.model.Role;
import br.com.senai.greenday.model.Usuario;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Role role;

    public static UsuarioDTO fromEntity(Usuario u) {
        return UsuarioDTO.builder()
                .id(u.getId())
                .nome(u.getNome())
                .email(u.getEmail())
                .telefone(u.getTelefone())
                .role(u.getRole())
                .build();
    }
}
