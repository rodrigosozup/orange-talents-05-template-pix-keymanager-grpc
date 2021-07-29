package br.com.keymanagergrpc.pix.remove

import br.com.keymanagergrpc.KeyManagerRemoveGrpcServiceGrpc
import br.com.keymanagergrpc.RemoveChavePixRequest
import br.com.keymanagergrpc.RemoveChavePixResponse
import br.com.keymanagergrpc.shared.grpc.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RemoveChaveEndPoint (@Inject private val service: RemoveChavePixService):
    KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceImplBase(){

    override fun remove(request: RemoveChavePixRequest,
                        responseObserver: StreamObserver<RemoveChavePixResponse>
    ) {

        service.remove(clienteId = request.clienteId, pixId = request.pixId)

        responseObserver.onNext(RemoveChavePixResponse.newBuilder()
            .setClienteId(request.clienteId)
            .setPixId(request.pixId)
            .build())

        responseObserver.onCompleted()
    }
}