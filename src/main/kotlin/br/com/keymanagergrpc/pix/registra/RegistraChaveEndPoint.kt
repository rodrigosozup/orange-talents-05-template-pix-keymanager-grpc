package br.com.keymanagergrpc.pix.registra

import br.com.keymanagergrpc.KeymangerRegistraGrpcServiceGrpc
import br.com.keymanagergrpc.RegistraChavePixRequest
import br.com.keymanagergrpc.RegistraChavePixResponse
import br.com.keymanagergrpc.shared.grpc.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RegistraChaveEndPoint(@Inject private val service: NovaChavePixService) :
    KeymangerRegistraGrpcServiceGrpc.KeymangerRegistraGrpcServiceImplBase(){

    override fun registra(
        request: RegistraChavePixRequest?,
        responseObserver: StreamObserver<RegistraChavePixResponse>?
    ) {
        val novaChave = request?.toModel()
        val chaveCriada = service.registra(novaChave)

        responseObserver?.onNext(RegistraChavePixResponse.newBuilder()
                                    .setClienteId(chaveCriada.clienteId.toString())
                                    .setPixId(chaveCriada.id.toString())
                                    .build())
        responseObserver?.onCompleted()
    }
}