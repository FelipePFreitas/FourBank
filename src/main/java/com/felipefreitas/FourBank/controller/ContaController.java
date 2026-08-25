package com.felipefreitas.FourBank.controller;

import com.felipefreitas.FourBank.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ContaController {

    private final ContaService contaService;

    @PostMapping("/pix")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Cadastrar chave Pix",
            description = "Cadastra uma chave Pix para a conta do usuário autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chave Pix cadastrada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    public ResponseEntity<Void> cadastrarChavePix(@AuthenticationPrincipal UserDetails user,
                                             @RequestBody String body) {
        contaService.cadastrarChavePix(user.getUsername(), body);
        return ResponseEntity.noContent().build();
    }
}
