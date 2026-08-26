package com.felipefreitas.FourBank.service;

import com.felipefreitas.FourBank.dto.transacao.TransacaoResponseDTO;
import com.felipefreitas.FourBank.entity.ContaEntity;
import com.felipefreitas.FourBank.entity.TransacaoEntity;
import com.felipefreitas.FourBank.enums.ErrorEnum;
import com.felipefreitas.FourBank.enums.StatusTransacao;
import com.felipefreitas.FourBank.enums.TipoTransacao;
import com.felipefreitas.FourBank.exceptions.BaseExceptions;
import com.felipefreitas.FourBank.repository.ContaRepository;
import com.felipefreitas.FourBank.repository.TransacaoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;


    @Transactional
    public TransacaoResponseDTO pix(String loginUsuarioAutenticado, String chavePix, BigDecimal valor) {

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BaseExceptions(ErrorEnum.SALDO_NEGATIVO_NULO);
        }

        ContaEntity contaOrigem = contaRepository.findByCliente_Usuario_Login(loginUsuarioAutenticado)
                .orElseThrow(() -> new BaseExceptions(ErrorEnum.NUMERO_CONTA_NAO_EXISTE));

        ContaEntity contaDestino = contaRepository.findByChavesPixContaining(chavePix)
                .orElseThrow(() -> new BaseExceptions(ErrorEnum.CHAVEPIX_INEXISTENTE));


        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new BaseExceptions(ErrorEnum.SALDO_INSUFICIENTE);
        }

        if (contaDestino.getId().equals(contaOrigem.getId())) {
            throw new BaseExceptions(ErrorEnum.TRANSACAO_MESMA_CONTA);
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));

        contaRepository.save(contaOrigem);


        TransacaoEntity transacao = TransacaoEntity
                .builder()
                .contaOrigem(contaOrigem)
                .contaDestino(contaDestino)
                .descricao("Transferência via PIX de " + valor + " da conta " + contaOrigem.getNumeroConta() + " para a conta " + contaDestino.getNumeroConta())
                .valor(valor)
                .tipoTransacao(TipoTransacao.PIX)
                .statusTransacao(StatusTransacao.PENDENTE)
                .criadoEm(LocalDateTime.now())
                .build();

        transacaoRepository.save(transacao);

        try {
            contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
            contaRepository.save(contaDestino);


            transacao.setStatusTransacao(StatusTransacao.CONCLUIDA);
            transacaoRepository.save(transacao);

        } catch (Exception e) {
            log.error("Erro ao processar transferência PIX. Estornando valor...", e);


            contaOrigem.setSaldo(contaOrigem.getSaldo().add(valor));
            transacao.setStatusTransacao(StatusTransacao.CANCELADA);

            transacaoRepository.saveAndFlush(transacao);
            contaRepository.saveAndFlush(contaOrigem);

            throw new BaseExceptions(ErrorEnum.TRANSACAO_PIX_FALHA);
        }

        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getTipoTransacao(),
                transacao.getValor(),
                transacao.getDescricao(),
                transacao.getCriadoEm(),
                transacao.getContaOrigem().getId(),
                transacao.getContaDestino().getId());
    }

}
