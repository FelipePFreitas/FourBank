package com.felipefreitas.FourBank.ports.in.transacao;

import com.felipefreitas.FourBank.domain.model.Conta;
import com.felipefreitas.FourBank.domain.model.Transacao;

import java.math.BigDecimal;

public interface PixUseCase {

    Transacao pix (String contaAutenticada, String chavePix, BigDecimal valor);
}
