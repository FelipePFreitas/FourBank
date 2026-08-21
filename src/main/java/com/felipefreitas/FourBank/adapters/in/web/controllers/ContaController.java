package com.felipefreitas.FourBank.adapters.in.web.controllers;

import com.felipefreitas.FourBank.ports.in.conta.CadastraChavePIxUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conta")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Endpoint Conta", description = "Endpoint das funções da conta")
public class ContaController {

    private final CadastraChavePIxUseCase cadastraChavePIxUseCase;


    @PostMapping("/{chavePix}")
    @Operation(summary = "Cadastrar chaves pix", description = "Cadastro de chave pix")
    public ResponseEntity<@NonNull Void> cadastrarChavePix(@AuthenticationPrincipal UserDetails userDetails,
                                                           @PathVariable String chavePix) {
        cadastraChavePIxUseCase.cadastrarChavePix(userDetails.getUsername(),chavePix);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
