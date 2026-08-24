package com.felipefreitas.FourBank.service;

import com.felipefreitas.FourBank.dto.cliente.ClientePFRequestDTO;
import com.felipefreitas.FourBank.dto.cliente.ClientePFResponseDTO;
import com.felipefreitas.FourBank.dto.endereco.EnderecoResponseDTO;
import com.felipefreitas.FourBank.dto.usuario.UsuarioResponseDTO;
import com.felipefreitas.FourBank.entity.ClientePFEntity;
import com.felipefreitas.FourBank.entity.EnderecosEntity;
import com.felipefreitas.FourBank.entity.UsuarioEntity;
import com.felipefreitas.FourBank.enums.ErrorEnum;
import com.felipefreitas.FourBank.enums.StatusCliente;
import com.felipefreitas.FourBank.exceptions.BaseExceptions;
import com.felipefreitas.FourBank.repository.ClientePFRepository;
import com.felipefreitas.FourBank.repository.UsuarioRepository;
import com.felipefreitas.FourBank.utils.CPFUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class ClientePFService {

    private final ClientePFRepository clientePFRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContaService contaService;


    @Transactional
    public ClientePFResponseDTO cadastroClientePF(ClientePFRequestDTO request) {
        if (!CPFUtil.isValid(request.cpf())) {
            throw new BaseExceptions(ErrorEnum.CPF_INVALIDO);
        }

        if (clientePFRepository.existsByDocumento(request.cpf())) {
            throw new BaseExceptions(ErrorEnum.CPF_JA_CADASTRADO);
        }

        if (clientePFRepository.existsByEmail(request.email())) {
            throw new BaseExceptions(ErrorEnum.CLIENTE_JA_CADASTRADO);
        }

        if (usuarioRepository.findByLogin(request.usuario().login()).isPresent()) {
            throw new BaseExceptions(ErrorEnum.LOGIN_JA_CADASTRADO);
        }

        LocalDateTime agora = LocalDateTime.now();

        EnderecosEntity endereco = new EnderecosEntity();

        endereco.setEndereco(request.endereco().endereco());
        endereco.setNumero(request.endereco().numero());
        endereco.setCep(request.endereco().cep());
        endereco.setBairro(request.endereco().bairro());
        endereco.setCidade(request.endereco().cidade());
        endereco.setUf(request.endereco().uf());

        ClientePFEntity clientePFEntity = new ClientePFEntity();
        clientePFEntity.setNomeRazaoSocial(request.nome());
        clientePFEntity.setDataNascimento(request.dataNascimento());
        clientePFEntity.setDocumento(request.cpf());
        clientePFEntity.setEmail(request.email());
        clientePFEntity.setTelefone(request.telefone());
        clientePFEntity.setCriadoEm(agora);
        clientePFEntity.setAtualizadoEm(agora);
        clientePFEntity.setStatusCliente(StatusCliente.ATIVO);
        clientePFEntity.setEndereco(endereco);

        ClientePFEntity clienteSalvo = clientePFRepository.save(clientePFEntity);

        UsuarioEntity usuario = UsuarioEntity.builder()
                .login(request.usuario().login())
                .senha(passwordEncoder.encode(request.usuario().senha()))
                .cliente(clienteSalvo)
                .build();

        UsuarioEntity usuarioSalvo = usuarioRepository.save(usuario);

        contaService.criarConta(clienteSalvo, BigDecimal.ZERO);

        return new ClientePFResponseDTO(
                clienteSalvo.getId(),
                clienteSalvo.getNomeRazaoSocial(),
                clienteSalvo.getDataNascimento(),
                clienteSalvo.getDocumento(),
                clienteSalvo.getEmail(),
                clienteSalvo.getTelefone(),
                clienteSalvo.getStatusCliente(),
                new EnderecoResponseDTO(
                        clienteSalvo.getEndereco().getId(),
                        clienteSalvo.getEndereco().getEndereco(),
                        clienteSalvo.getEndereco().getNumero(),
                        clienteSalvo.getEndereco().getCep(),
                        clienteSalvo.getEndereco().getBairro(),
                        clienteSalvo.getEndereco().getCidade(),
                        clienteSalvo.getEndereco().getUf()
                ),
                new UsuarioResponseDTO(
                        usuarioSalvo.getId(),
                        usuarioSalvo.getLogin()
                )
        );

    }
}
