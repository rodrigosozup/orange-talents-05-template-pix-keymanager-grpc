package br.com.keymanagergrpc.pix.registra

import br.com.keymanagergrpc.TipoDeChave
import br.com.keymanagergrpc.TipoDeConta
import br.com.keymanagergrpc.pix.ChavePix
import br.com.keymanagergrpc.pix.ContaAssociada
import br.com.keymanagergrpc.shared.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

//@ValidPixKey
@Introspected
data class NovaChavePix(
    @field:ValidUUID
    @field:NotBlank
    val clienteId: String?,
    @field:NotNull
    val tipo: TipoDeChave?,
    @field:Size(max = 77)
    val chave: String?,
    @field:NotNull
    val tipoDeConta: TipoDeConta?
) {

    fun toModel(conta: ContaAssociada): ChavePix {
        return ChavePix(
            clienteId = UUID.fromString(this.clienteId),
            tipo = TipoDeChave.valueOf(this.tipo!!.name),
            chave = if (this.tipo == TipoDeChave.ALEATORIA) UUID.randomUUID().toString() else this.chave!!,
            tipoDeConta = TipoDeConta.valueOf(this.tipoDeConta!!.name),
            conta = conta
        )
    }

}