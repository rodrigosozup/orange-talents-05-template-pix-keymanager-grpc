package br.com.keymanagergrpc.pix.registra

import br.com.keymanagergrpc.integration.itau.ContasDeClientesNoItauClient
import br.com.keymanagergrpc.pix.ChavePix
import br.com.keymanagergrpc.pix.ChavePixExistenteException
import br.com.keymanagergrpc.pix.ChavePixRepository
import br.com.keymanagergrpc.pix.ClienteNaoEncontradoException
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class NovaChavePixService(@Inject val repository: ChavePixRepository,
                          @Inject val itauClient: ContasDeClientesNoItauClient
) {

    @Transactional //Forçou a colocar o @Validated
    fun registra(@Valid novaChave: NovaChavePix?) : ChavePix{

        // 1. verifica se chave já existe no sistema
        if(repository.existsByChave(novaChave?.chave)){
            throw ChavePixExistenteException("Chave Pix '${novaChave?.chave}' existente")
        }

        //Busca dados da conta no ERP do Itau
        val respose = itauClient.buscarContaPorTipo(novaChave?.clienteId!!,novaChave?.tipoDeConta?.name!!)
        val conta = respose.body()?.toModel() ?: throw ClienteNaoEncontradoException("Cliente não encontrado no Itau")

        val chave = novaChave.toModel(conta)
        repository.save(chave)

       // val testeIDUUID = repository.findByChave(chave.chave)
        val testeIDUUID = repository.findById(chave.id)
        println(testeIDUUID)

        return chave
    }
}