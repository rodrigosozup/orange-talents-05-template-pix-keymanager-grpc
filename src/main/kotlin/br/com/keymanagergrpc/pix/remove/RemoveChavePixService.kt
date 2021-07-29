package br.com.keymanagergrpc.pix.remove

import br.com.keymanagergrpc.pix.ChavePixNaoEncontradaException
import br.com.keymanagergrpc.pix.ChavePixRepository
import br.com.keymanagergrpc.shared.validation.ValidUUID
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Singleton
@Validated
class RemoveChavePixService (@Inject val repository: ChavePixRepository){

    @Transactional
    fun remove(@NotBlank @ValidUUID clienteId: String,
               @NotBlank @ValidUUID pixId: String){

        val uuidPixId = UUID.fromString(pixId)
        val uuidClienteId = UUID.fromString(clienteId)

        val chaveOptional =  repository.findByIdAndClienteId(uuidPixId, uuidClienteId)
            .orElseThrow{ ChavePixNaoEncontradaException("Chave Pix não encontrada ou não pertence ao cliente") }

        repository.delete(chaveOptional)

    }
}