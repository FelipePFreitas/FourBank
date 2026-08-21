package com.felipefreitas.FourBank.ports.out;

import com.felipefreitas.FourBank.domain.model.Transacao;

public interface TransacaoPort {

    Transacao save(Transacao transacao);

}
