package br.com.keymanagergrpc.pix.remove

import br.com.keymanagergrpc.KeyManagerRemoveGrpcServiceGrpc
import br.com.keymanagergrpc.RemoveChavePixRequest
import br.com.keymanagergrpc.TipoDeChave
import br.com.keymanagergrpc.TipoDeConta
import br.com.keymanagergrpc.pix.ChavePix
import br.com.keymanagergrpc.pix.ChavePixRepository
import br.com.keymanagergrpc.pix.ContaAssociada
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RemoveChaveEndPointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub
) {
    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setup() {
        CHAVE_EXISTENTE = repository.save(
            ChavePix(
                clienteId = UUID.randomUUID(),
                tipo = TipoDeChave.EMAIL,
                chave = "rponte@gmail.com",
                tipoDeConta = TipoDeConta.CONTA_CORRENTE,
                conta = ContaAssociada(
                    instituicao = "Itau",
                    nomeDoTitular = "Rafael Ponte",
                    cpfDoTitular = "11111111",
                    agencia = "3324",
                    numeroDaConta = "144568"
                )
            )
        )
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve remover chave pix existente`() {
        //Açao
        val response = grpcClient.remove(
            RemoveChavePixRequest.newBuilder()
                .setClienteId(CHAVE_EXISTENTE.clienteId.toString())
                .setPixId(CHAVE_EXISTENTE.id.toString())
                .build()
        )

        with(response) {
            assertEquals(CHAVE_EXISTENTE.clienteId.toString(), clienteId)
            assertEquals(CHAVE_EXISTENTE.id.toString(), pixId)
        }
    }

    @Test
    internal fun `nao deve remover chave pix quando chave inexistente`() {
        //Cenario
        val pixIdNaoExistente = UUID.randomUUID().toString()

        //Ação
        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.remove(
                RemoveChavePixRequest.newBuilder()
                    .setPixId(pixIdNaoExistente)
                    .setClienteId(CHAVE_EXISTENTE.clienteId.toString())
                    .build()
            )
        }

        with(thrown){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("Chave Pix não encontrada ou não pertence ao cliente",status.description)
        }
    }

    @Test
    fun `nao deve remover chave pix quando chave existente mas pertence a outro cliente`() {
        // cenário
        val outroClienteId = UUID.randomUUID().toString()

        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.remove(RemoveChavePixRequest.newBuilder()
                .setPixId(CHAVE_EXISTENTE.id.toString())
                .setClienteId(outroClienteId)
                .build())
        }

        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix não encontrada ou não pertence ao cliente", status.description)
        }
    }


    @Test
    fun `nao deve remover chave pix quando parametros inválidos`() {
        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.remove(RemoveChavePixRequest.newBuilder().build())
        }

        // validação
        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados inválidos", status.description)
//            assertThat(violations(), containsInAnyOrder(
//                Pair("pixId", "must not be blank"),
//                Pair("clienteId", "must not be blank"),
//                Pair("pixId", "não é um formato válido de UUID"),
//                Pair("clienteId", "não é um formato válido de UUID"),
//            ))
        }
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub? {
            return KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }


}