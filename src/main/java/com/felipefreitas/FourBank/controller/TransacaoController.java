package com.felipefreitas.FourBank.controller;

import com.felipefreitas.FourBank.dto.transacao.TransacaoResponseDTO;
import com.felipefreitas.FourBank.dto.transacao.TransferenciaRequestDTO;
import com.felipefreitas.FourBank.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transações", description = "Endpoints de transações bancárias do usuário autenticado")
public class TransacaoController {

    private final TransacaoService transacaoService;

    @PostMapping("/transferencias")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Realizar transferência", description = "Transfere ou agenda valor para uma conta FourBank existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferência concluída ou agendada",
                    content = @Content(schema = @Schema(implementation = TransacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "422", description = "Transferência não permitida", content = @Content)
    })
    public ResponseEntity<TransacaoResponseDTO> transferir(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody @jakarta.validation.Valid TransferenciaRequestDTO request) {
        return ResponseEntity.ok(transacaoService.transferir(user.getUsername(), request));
    }

    @PostMapping("/pix/{chavePix}/{valor}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Transferir via Pix", description = "Realiza uma transferência Pix para a chave informada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferência Pix realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = TransacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para a transação", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta de destino não encontrada", content = @Content)
    })
    public ResponseEntity<TransacaoResponseDTO> pix(@AuthenticationPrincipal UserDetails user,
                                                    @PathVariable String chavePix,
                                                    @PathVariable BigDecimal valor) {
        TransacaoResponseDTO transacaoResponseDTO = transacaoService.pix(user.getUsername(), chavePix, valor);
        return ResponseEntity.ok(transacaoResponseDTO);
    }


    @PostMapping("/deposito/{valor}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Depositar valor", description = "Realiza um depósito na conta do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = TransacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para a transação", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    public ResponseEntity<TransacaoResponseDTO> deposito(@AuthenticationPrincipal UserDetails user,
                                                         @PathVariable BigDecimal valor) {

        TransacaoResponseDTO transacaoResponseDTO = transacaoService.depositar(user.getUsername(), valor);
        return ResponseEntity.ok(transacaoResponseDTO);
    }


    @PostMapping("/saque/{valor}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Sacar valor", description = "Realiza um saque na conta do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saque realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = TransacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para a transação", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    public ResponseEntity<TransacaoResponseDTO> saque(@AuthenticationPrincipal UserDetails user,
                                                         @PathVariable BigDecimal valor) {

        TransacaoResponseDTO transacaoResponseDTO = transacaoService.saque(user.getUsername(), valor);
        return ResponseEntity.ok(transacaoResponseDTO);
    }

}
