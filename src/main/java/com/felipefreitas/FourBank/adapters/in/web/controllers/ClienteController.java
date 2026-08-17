package com.felipefreitas.FourBank.adapters.in.web.controllers;

import com.felipefreitas.FourBank.adapters.in.web.dto.cliente.ClienteRequestDTO;
import com.felipefreitas.FourBank.adapters.in.web.dto.cliente.ClienteResponseDTO;
import com.felipefreitas.FourBank.adapters.out.persistence.mapper.ClienteMapper;
import com.felipefreitas.FourBank.domain.model.Cliente;
import com.felipefreitas.FourBank.ports.in.cliente.CadastrarClientePFUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cliente")
@RequiredArgsConstructor
@Tag(name = "Endpoint cliente", description = "Endpoint das funções do cliente")
public class ClienteController {

    private final CadastrarClientePFUseCase cadastrarClientePFUseCase;
    private final ClienteMapper clienteMapper;

    @PostMapping
    @Operation(summary = "Cadastro de cliente", description = "Cliente se cadastra e cria sua conta")
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {

        Cliente cliente = clienteMapper.toClienteDomain(clienteRequestDTO);

        Cliente clienteSalvo = cadastrarClientePFUseCase.cadastrarClientePF(cliente);

        ClienteResponseDTO clienteResponseDTO = clienteMapper.toClienteResponseDTO(clienteSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(clienteResponseDTO);
    }
}