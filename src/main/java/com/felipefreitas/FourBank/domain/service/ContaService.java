package com.felipefreitas.FourBank.domain.service;

import com.felipefreitas.FourBank.domain.enums.ErrorEnum;
import com.felipefreitas.FourBank.domain.enums.TipoConta;
import com.felipefreitas.FourBank.domain.exception.BaseException;
import com.felipefreitas.FourBank.domain.model.Cliente;
import com.felipefreitas.FourBank.domain.model.Conta;
import com.felipefreitas.FourBank.ports.in.conta.CadastraChavePIxUseCase;
import com.felipefreitas.FourBank.ports.out.ClientePort;
import com.felipefreitas.FourBank.ports.out.ContaPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class ContaService implements CadastraChavePIxUseCase {

    private final ContaPort contaPort;
    private final ClientePort clientePort;

    @Override
        @Transactional
        public void cadastrarChavePix(String usuarioAutenticado, String novaChave) {

            // 1. Sanitização básica
            if (novaChave == null || novaChave.isBlank()) {
                throw new BaseException(ErrorEnum.CHAVE_PIX_NULO_BRANCA);
            }

            String chaveSanitizada = novaChave.trim();

            if (chaveSanitizada.length() > 77) {
                throw new BaseException(ErrorEnum.CARACTERES_ACIMA);
            }

            Cliente cliente = clientePort.findByEmail(usuarioAutenticado)
                    .orElseThrow(() -> new BaseException(ErrorEnum.CONTA_NAO_ENCONTRADA));

            // 3. Regra do BACEN para limite de chaves (PF = 5, PJ = 20)
            int limiteChaves = (cliente.getConta().getTipoConta() == TipoConta.PJ) ? 20 : 5;

            if (cliente.getConta().getChavesPix().size() >= limiteChaves) {
                throw new BaseException(ErrorEnum.LIMITE_CHAVE_PIX);
            }

            // 4. Valida se a chave Pix já pertence a QUALQUER outra conta no banco
            if (contaPort.findByChavePix(chaveSanitizada).isPresent()) {
                throw new BaseException(ErrorEnum.CHAVE_PIX_JA_CADASTRADA);
            }

            // 5. Adiciona no Set local e persiste a conta atualizada
            if (!cliente.getConta().getChavesPix().add(chaveSanitizada)) {
                throw new BaseException(ErrorEnum.CHAVE_PIX_JA_CADASTRADA);
            }

            contaPort.save(cliente.getConta());
            log.info("Chave Pix [{}] cadastrada com sucesso para a conta de id {}", chaveSanitizada, cliente.getConta().getId());
        }
}

