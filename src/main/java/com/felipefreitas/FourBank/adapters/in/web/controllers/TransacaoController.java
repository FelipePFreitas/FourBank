package com.felipefreitas.FourBank.adapters.in.web.controllers;

import com.felipefreitas.FourBank.adapters.in.web.dto.transacao.TransacaoResponseDTO;
import com.felipefreitas.FourBank.adapters.out.persistence.mapper.TransacaoMapper;
import com.felipefreitas.FourBank.domain.model.Transacao;
import com.felipefreitas.FourBank.ports.in.transacao.PixUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transacao")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Endpoint Transação", description = "Endpoint das funções da conta")
public class TransacaoController {
    private final PixUseCase pixUseCase;
    private final TransacaoMapper transacaoMapper;

    @PostMapping("/pix")
    @Operation(summary = "Realizar pix", description = "Realizar um pix via desde que tenha uma chave pix existente")
    public ResponseEntity<TransacaoResponseDTO> pix (@PathVariable String contaOrigem, @PathVariable String chavePix,
                                                     @PathVariable BigDecimal valor){
        Transacao transacao = pixUseCase.pix(contaOrigem,chavePix,valor);
        TransacaoResponseDTO transacaoResponseDTO = transacaoMapper.toResponse(transacao);

        return ResponseEntity.status(HttpStatus.CREATED).body(transacaoResponseDTO);
    }
}
