package com.felipefreitas.FourBank.service;

import com.felipefreitas.FourBank.dto.cliente.ClientePJRequestDTO;
import com.felipefreitas.FourBank.dto.cliente.ClientePJResponseDTO;
import com.felipefreitas.FourBank.dto.endereco.EnderecoResponseDTO;
import com.felipefreitas.FourBank.dto.usuario.UsuarioResponseDTO;
import com.felipefreitas.FourBank.entity.ClientePJEntity;
import com.felipefreitas.FourBank.entity.EnderecosEntity;
import com.felipefreitas.FourBank.entity.UsuarioEntity;
import com.felipefreitas.FourBank.enums.ErrorEnum;
import com.felipefreitas.FourBank.enums.StatusCliente;
import com.felipefreitas.FourBank.exceptions.BaseExceptions;
import com.felipefreitas.FourBank.repository.ClientePJRepository;
import com.felipefreitas.FourBank.repository.UsuarioRepository;
import com.felipefreitas.FourBank.utils.CNPJUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class ClientePJService {

    private final ClientePJRepository clientePJRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ClientePJResponseDTO cadastroClientePJ(ClientePJRequestDTO request) {
        log.info("Iniciando cadastro de cliente PJ para login={}", request.usuario().login());

        if (!CNPJUtil.isValid(request.cnpj())) {
            log.warn("Falha no cadastro PJ: CNPJ inválido para login={}", request.usuario().login());
            throw new BaseExceptions(ErrorEnum.CNPJ_INVALIDO);
        }

        if (clientePJRepository.existsByDocumento(request.cnpj())) {
            log.warn("Falha no cadastro PJ: CNPJ já cadastrado para login={}", request.usuario().login());
            throw new BaseExceptions(ErrorEnum.CLIENTE_JA_CADASTRADO);
        }

        if (clientePJRepository.existsByEmail(request.email())) {
            log.warn("Falha no cadastro PJ: e-mail já cadastrado para login={}", request.usuario().login());
            throw new BaseExceptions(ErrorEnum.CLIENTE_JA_CADASTRADO);
        }

        if (usuarioRepository.findByLogin(request.usuario().login()).isPresent()) {
            log.warn("Falha no cadastro PJ: login já cadastrado login={}", request.usuario().login());
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

        ClientePJEntity clientePJEntity = new ClientePJEntity();
        clientePJEntity.setNomeRazaoSocial(request.razaoSocial());
        clientePJEntity.setNomeFantasia(request.nomeFantasia());
        clientePJEntity.setDataFundacao(request.dataFundacao());
        clientePJEntity.setFaturamentoAnual(request.faturamentoAnual());
        clientePJEntity.setDocumento(request.cnpj());
        clientePJEntity.setEmail(request.email());
        clientePJEntity.setTelefone(request.telefone());
        clientePJEntity.setCriadoEm(agora);
        clientePJEntity.setAtualizadoEm(agora);
        clientePJEntity.setStatusCliente(StatusCliente.ATIVO);
        clientePJEntity.setEndereco(endereco);

        ClientePJEntity clienteSalvo = clientePJRepository.save(clientePJEntity);

        UsuarioEntity usuario = UsuarioEntity.builder()
                .login(request.usuario().login())
                .senha(passwordEncoder.encode(request.usuario().senha()))
                .cliente(clienteSalvo)
                .build();
        clienteSalvo.setUsuario(usuario);

        UsuarioEntity usuarioSalvo = usuarioRepository.save(usuario);
        log.info("Cadastro PJ concluído com sucesso. clienteId={} usuarioId={}",
                clienteSalvo.getId(), usuarioSalvo.getId());

        return new ClientePJResponseDTO(
                clienteSalvo.getId(),
                clienteSalvo.getNomeRazaoSocial(),
                clienteSalvo.getNomeFantasia(),
                clienteSalvo.getDataFundacao(),
                clienteSalvo.getFaturamentoAnual(),
                clienteSalvo.getDocumento(),
                clienteSalvo.getEmail(),
                clienteSalvo.getTelefone(),
                clienteSalvo.getStatusCliente(),
                clienteSalvo.getClienteTipo(),
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
