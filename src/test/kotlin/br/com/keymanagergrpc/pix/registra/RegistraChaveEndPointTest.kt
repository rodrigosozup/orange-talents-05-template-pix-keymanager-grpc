package br.com.keymanagergrpc.pix.registra

import br.com.keymanagergrpc.KeymangerRegistraGrpcServiceGrpc
import br.com.keymanagergrpc.RegistraChavePixRequest
import br.com.keymanagergrpc.TipoDeChave
import br.com.keymanagergrpc.TipoDeConta
import br.com.keymanagergrpc.integration.itau.ContasDeClientesNoItauClient
import br.com.keymanagergrpc.integration.itau.DadosDaContaResponse
import br.com.keymanagergrpc.integration.itau.InstituicaoResponse
import br.com.keymanagergrpc.integration.itau.TitularResponse
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
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import org.mockito.Mockito.`when`

/**
 * TIP: Necessario desabilitar o controle transacional (transactional=false) pois o gRPC Server
 * roda numa thread separada, caso contrário não será possível preparar cenário dentro do método @Test
 */
@MicronautTest(transactional = false)
internal class RegistraChaveEndPointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeymangerRegistraGrpcServiceGrpc.KeymangerRegistraGrpcServiceBlockingStub
) {
//    @Inject
//    lateinit var repository: ChavePixRepository

    @Inject
    lateinit var itauClient: ContasDeClientesNoItauClient

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }


    @Test
    internal fun ` regisdevetrar uma nova chave pix`() {
        `when`(itauClient.buscarContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse()))

        // ação
        val response = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID.toString())
                .setTipoDeChave(TipoDeChave.EMAIL)
                .setChave("rponte@gmail.com")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        // validação
        with(response) {
            assertEquals(CLIENTE_ID.toString(), clienteId)
            assertNotNull(pixId)
        }

    }

    @Test
    internal fun `nao deve registrar chave pix quando chave existente`() {

        //Cenario . Salvar uma chave
       repository.save(criarChavePix())

        //Ação
        val throws = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoDeChave(TipoDeChave.EMAIL)
                    .setChave("rponte@gmail.com")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(throws){
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("Chave Pix 'rponte@gmail.com' existente", status.description)
        }


    }

    @Test
    internal fun `nao deve registrar chave pix quando nao encontrar dados da conta cliente`() {

        //Cenario . Chamada para o ItauClient retornando notfound
        `when`(itauClient.buscarContaPorTipo(clienteId = CLIENTE_ID.toString(), "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        //Acao.  Chamada para o servico grpc
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoDeChave(TipoDeChave.EMAIL)
                    .setChave("rponte@gmail.com")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(thrown){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("Cliente não encontrado no Itau",status.description)
        }

    }

    private fun dadosDaContaResponse(): DadosDaContaResponse {
        return DadosDaContaResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", ContaAssociada.ITAU_UNIBANCO_ISPB),
            agencia = "1218",
            numero = "291900",
            titular = TitularResponse("Rafael Ponte", "63657520325")
        )
    }

    @MockBean(ContasDeClientesNoItauClient::class)
    fun itauClient(): ContasDeClientesNoItauClient {
        return Mockito.mock(ContasDeClientesNoItauClient::class.java)
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                KeymangerRegistraGrpcServiceGrpc.KeymangerRegistraGrpcServiceBlockingStub? {
            return KeymangerRegistraGrpcServiceGrpc.newBlockingStub(channel)
        }
    }


    fun criarChavePix() : ChavePix {
        return ChavePix(
            clienteId = CLIENTE_ID,
            tipo = TipoDeChave.EMAIL,
            chave = "rponte@gmail.com",
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                instituicao = "Itau",
                nomeDoTitular = "Rafael Ponte",
                cpfDoTitular = "11111111",
                agencia =  "3324",
                numeroDaConta = "144568"
            )
        )
    }


}