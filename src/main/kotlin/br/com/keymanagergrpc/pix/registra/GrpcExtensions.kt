package br.com.keymanagergrpc.pix.registra

import br.com.keymanagergrpc.RegistraChavePixRequest
import br.com.keymanagergrpc.TipoDeChave
import br.com.keymanagergrpc.TipoDeConta

fun RegistraChavePixRequest.toModel() : NovaChavePix {
    return NovaChavePix( // 1
        clienteId = clienteId,
        tipo = when (tipoDeChave) {
            TipoDeChave.UNKNOWN_TIPO_CHAVE -> null
            else -> TipoDeChave.valueOf(tipoDeChave.name) // 1
        },
        chave = chave,
        tipoDeConta = when (tipoDeConta) {
            TipoDeConta.UNKNOWN_TIPO_CONTA -> null
            else -> TipoDeConta.valueOf(tipoDeConta.name) // 1
        }
    )
}