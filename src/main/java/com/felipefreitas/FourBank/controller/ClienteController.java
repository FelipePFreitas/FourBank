package com.felipefreitas.FourBank.controller;

import com.felipefreitas.FourBank.dto.cliente.ClientePFRequestDTO;
import com.felipefreitas.FourBank.dto.cliente.ClientePFResponseDTO;
import com.felipefreitas.FourBank.dto.cliente.ClientePJRequestDTO;
import com.felipefreitas.FourBank.dto.cliente.ClientePJResponseDTO;
import com.felipefreitas.FourBank.service.ClientePFService;
import com.felipefreitas.FourBank.service.ClientePJService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
@AllArgsConstructor
public class ClienteController {

    private final ClientePFService clientePFService;
    private final ClientePJService clientePJService;

    @PostMapping("/pf")
    public ResponseEntity<ClientePFResponseDTO> cadastrarClientePF(@RequestBody @Valid ClientePFRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientePFService.cadastroClientePF(request));
    }

    @PostMapping("/pj")
    public ResponseEntity<ClientePJResponseDTO> cadastrarClientePJ(@RequestBody @Valid ClientePJRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientePJService.cadastroClientePJ(request));
    }
}
