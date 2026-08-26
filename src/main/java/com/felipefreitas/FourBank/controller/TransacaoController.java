package com.felipefreitas.FourBank.controller;

import com.felipefreitas.FourBank.dto.transacao.TransacaoResponseDTO;
import com.felipefreitas.FourBank.service.TransacaoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransacaoController {

    private final TransacaoService transacaoService;

    @PostMapping("/pix/{chavePix}/{valor}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransacaoResponseDTO> pix (@AuthenticationPrincipal UserDetails user, @PathVariable String chavePix, @PathVariable BigDecimal valor){
        TransacaoResponseDTO transacaoResponseDTO = transacaoService.pix(user.getUsername(), chavePix, valor);
        return ResponseEntity.ok(transacaoResponseDTO);
    }

}
