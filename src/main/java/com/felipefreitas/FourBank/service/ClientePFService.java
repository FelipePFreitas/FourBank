package com.felipefreitas.FourBank.service;

import com.felipefreitas.FourBank.dto.cliente.ClientePFResponseDTO;
import com.felipefreitas.FourBank.entity.ClientePFEntity;
import com.felipefreitas.FourBank.enums.ClienteTipo;
import com.felipefreitas.FourBank.enums.ErrorEnum;
import com.felipefreitas.FourBank.exceptions.BaseExceptions;
import com.felipefreitas.FourBank.repository.ClientePFRepository;
import com.felipefreitas.FourBank.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ClientePFService {

    private final ClientePFRepository clientePFRepository;
    private final UsuarioRepository usuarioRepository;


    public ClientePFResponseDTO cadastroClientePF(ClientePFEntity clientePFEntity) {

       if(clientePFRepository.existsByCpf(clientePFEntity.getDocumento())){
           throw new BaseExceptions(ErrorEnum.CPF_JA_CADASTRADO);
       }

        clientePFEntity.setClienteTipo(ClienteTipo.PESSOA_FISICA);
        return null;
    }
}

