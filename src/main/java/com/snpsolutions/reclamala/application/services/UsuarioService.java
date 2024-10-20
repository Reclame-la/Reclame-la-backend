package com.snpsolutions.reclamala.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.regex.Pattern;


import com.snpsolutions.reclamala.domain.dtos.UsuarioDTO;
import com.snpsolutions.reclamala.domain.entities.Usuario;
import com.snpsolutions.reclamala.domain.enums.UsuarioTipo;
import com.snpsolutions.reclamala.domain.repositories.UsuarioRepository;
import com.snpsolutions.reclamala.infra.handles.UsuarioJaCadastradoException;
import com.snpsolutions.reclamala.infra.handles.UsuarioTipoDiferenteException;


@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@souunit\\.com\\.br$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Transactional
    public Usuario cadastrarUsuarioAluno(UsuarioDTO usuarioDTO) {

        if (usuarioRepository.existsByMatricula(usuarioDTO.getMatricula())) {
            throw new UsuarioJaCadastradoException("Usuário com matrícula " + usuarioDTO.getMatricula() + " já cadastrado.");
        }

        if (!emailEhValido(usuarioDTO.getEmail())) {
            throw new IllegalArgumentException("Email inválido. O email deve ser do domínio @souunit.com.br.");
        }
        if (usuarioRepository.existsByUsuarioCpf(usuarioDTO.getUsuarioCpf())) {
            throw new UsuarioJaCadastradoException("Usuário com CPF " + usuarioDTO.getUsuarioCpf() + " já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setMatricula(usuarioDTO.getMatricula());
        usuario.setUsuarioCpf(usuarioDTO.getUsuarioCpf());
        usuario.setUsername(usuarioDTO.getUsername());
        usuario.setPassword(criptografarSenha(usuarioDTO.getPassword()));

        if (usuarioDTO.getTipoUsuario() != UsuarioTipo.ALUNO) {
            throw new UsuarioTipoDiferenteException("Tipo do Usuario" + usuarioDTO.getTipoUsuario() 
                                                    + "Não é aceitavel" +  "Tipo aceitavel é: " + usuarioDTO.getTipoUsuario().ALUNO);
        }
        usuario.setTipoUsuario(usuarioDTO.getTipoUsuario());
        
        return usuarioRepository.save(usuario);
    }

    public String criptografarSenha(String senha){
       
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(senha);
    }

   public boolean emailEhValido(String email){
        return email != null && EMAIL_PATTERN.matcher(email).matches();
   }


}
