package br.com.keymanagergrpc.pix

import br.com.keymanagergrpc.TipoDeChave
import br.com.keymanagergrpc.TipoDeConta
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/*
@Table(uniqueConstraints = [UniqueConstraint(
    name = "uk_chave_pix",
    columnNames = ["chave"]
)])
* */
@Entity
class ChavePix(
    @field:NotNull
    @Column(nullable = false) // length = 16  -  para o mysql
    val clienteId: UUID,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipo: TipoDeChave,

    @field:NotBlank
    @Column(unique = true, nullable = false)
    var chave: String,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoDeConta: TipoDeConta,

    @field:Valid
    @Embedded
    val conta: ContaAssociada
) {
    @field:Id
    @field:GeneratedValue
    val id: UUID? = null

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()

    override fun toString(): String {
        return "ChavePix(clienteId=$clienteId, tipo=$tipo, chave='$chave', tipoDeConta=$tipoDeConta, conta=$conta, id=$id, criadaEm=$criadaEm)"
    }

    /**
     * Verifica se esta chave pertence a este cliente
     */
    fun pertenceAo(clienteId: UUID) = this.clienteId.equals(clienteId)

    /**
     * Verifica se é chave uma aleatória
     */
    fun isAleatoria(): Boolean {
        return tipo == TipoDeChave.ALEATORIA
    }

    /**
     * Atualiza a valor da chave. Somente chave do tipo ALEATORIA pode
     * ser alterado.
     */
    fun atualiza(chave: String): Boolean {
        if (isAleatoria()) {
            this.chave = chave
            return true
        }
        return false
    }

}