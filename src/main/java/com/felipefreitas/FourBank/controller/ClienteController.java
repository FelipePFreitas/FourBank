package com.felipefreitas.FourBank.controller;

import com.felipefreitas.FourBank.dto.cliente.ClientePFRequestDTO;
import com.felipefreitas.FourBank.dto.cliente.ClientePFResponseDTO;
import com.felipefreitas.FourBank.dto.cliente.ClientePJRequestDTO;
import com.felipefreitas.FourBank.dto.cliente.ClientePJResponseDTO;
import com.felipefreitas.FourBank.service.ClientePFService;
import com.felipefreitas.FourBank.service.ClientePJService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Clientes", description = "Endpoints de cadastro de clientes PF e PJ")
public class ClienteController {

    private final ClientePFService clientePFService;
    private final ClientePJService clientePJService;

    @PostMapping("/pf")
    @Operation(summary = "Cadastrar cliente pessoa física", description = "Realiza o cadastro de cliente PF e cria o usuário de login no mesmo fluxo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente PF cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClientePFResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "CPF ou login já cadastrado", content = @Content)
    })
    public ResponseEntity<ClientePFResponseDTO> cadastrarClientePF(@RequestBody @Valid ClientePFRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientePFService.cadastroClientePF(request));
    }

    @PostMapping("/pj")
    @Operation(summary = "Cadastrar cliente pessoa jurídica", description = "Realiza o cadastro de cliente PJ e cria o usuário de login no mesmo fluxo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente PJ cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClientePJResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "CNPJ ou login já cadastrado", content = @Content)
    })
    public ResponseEntity<ClientePJResponseDTO> cadastrarClientePJ(@RequestBody @Valid ClientePJRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientePJService.cadastroClientePJ(request));
    }
}
