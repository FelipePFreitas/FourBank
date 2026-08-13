package com.felipefreitas.FourBank.adapters.in.web.controllers;

import com.felipefreitas.FourBank.adapters.in.web.dto.cliente.ClienteRequestDTO;
import com.felipefreitas.FourBank.adapters.in.web.dto.cliente.ClienteResponseDTO;
import com.felipefreitas.FourBank.adapters.out.persistence.mapper.ClienteMapper;
import com.felipefreitas.FourBank.domain.model.Cliente;
import com.felipefreitas.FourBank.domain.service.ClienteService;
import com.felipefreitas.FourBank.ports.in.cliente.CadastrarClientePFUseCase;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteController {
    private final CadastrarClientePFUseCase cadastrarClientePFUseCase;
    private final ClienteMapper clienteMapper;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente (@Valid @RequestBody ClienteRequestDTO clienteRequestDTO){

        Cliente cliente = clienteMapper.toClienteDomain(clienteRequestDTO);

        Cliente clienteSalvo = cadastrarClientePFUseCase.cadastrarClientePF(cliente);

        ClienteResponseDTO clienteResponseDTO = clienteMapper.toClienteResponseDTO(clienteSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(clienteResponseDTO);
    }

}
