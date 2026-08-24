package com.felipefreitas.FourBank.controller;

import com.felipefreitas.FourBank.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @PostMapping("/pix")
    public ResponseEntity<Void> cadastrarPix(@AuthenticationPrincipal UserDetails user,
                                             @PathVariable String body) {
        contaService.cadastrarChavePix(user.getUsername(), body);
        return ResponseEntity.noContent().build();
    }
}
