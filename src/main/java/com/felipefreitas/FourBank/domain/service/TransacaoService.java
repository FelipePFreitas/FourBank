package com.felipefreitas.FourBank.domain.service;

import com.felipefreitas.FourBank.domain.enums.ErrorEnum;
import com.felipefreitas.FourBank.domain.enums.TipoTransacao;
import com.felipefreitas.FourBank.domain.exception.BaseException;
import com.felipefreitas.FourBank.domain.model.Conta;
import com.felipefreitas.FourBank.domain.model.Transacao;
import com.felipefreitas.FourBank.ports.in.transacao.PixUseCase;
import com.felipefreitas.FourBank.ports.out.ContaPort;
import com.felipefreitas.FourBank.ports.out.TransacaoPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor

public class TransacaoService implements PixUseCase {

    private final TransacaoPort transacaoPort;
    private final ContaPort contaPort;


    @Override
    @Transactional
    public Transacao pix(String contaAutenticada, String chavePix, BigDecimal valor) {

        Conta contaOrigem =
                contaPort.findByNumeroConta(contaAutenticada).orElseThrow(() -> new BaseException(ErrorEnum.CONTA_NAO_ENCONTRADA));

        Conta contaDestino =
                contaPort.findByChavePix(chavePix).orElseThrow(() -> new BaseException(ErrorEnum.CHAVE_PIX_NAO_ENCONTRADA));

        if (contaOrigem.getNumeroConta().equals(contaDestino.getNumeroConta())) {
            throw new BaseException(ErrorEnum.TRANSACAO_MESMA_CONTA);
        }

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BaseException(ErrorEnum.VALOR_INVALIDO);
        }

        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new BaseException(ErrorEnum.SALDO_INSUFICIENTE);
        } else {
            BigDecimal valorConta1 = contaOrigem.getSaldo().subtract(valor);
            contaOrigem.setSaldo(valorConta1);
            BigDecimal valorConta2 = contaDestino.getSaldo().add(valor);
            contaDestino.setSaldo(valorConta2);
        }

        contaPort.save(contaOrigem);
        contaPort.save(contaDestino);

        Transacao transacao = Transacao.builder()
                .tipoTransacao(TipoTransacao.PIX)
                .contaOrigem(contaOrigem)
                .contaDestino(contaDestino)
                .dataHora(LocalDateTime.now())
                .valor(valor)
                .build();

        Transacao transacaoSalva = transacaoPort.save(transacao);

        return transacaoSalva;
    }
}
