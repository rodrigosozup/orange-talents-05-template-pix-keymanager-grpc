package br.com.keymanagergrpc.shared.grpc.handlers

import br.com.keymanagergrpc.pix.ChavePixExistenteException
import br.com.keymanagergrpc.shared.grpc.ExceptionHandler
import br.com.keymanagergrpc.shared.grpc.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChavePixExistenteExceptionHandler : ExceptionHandler<ChavePixExistenteException> {

    override fun handle(e: ChavePixExistenteException): StatusWithDetails {
        return StatusWithDetails(
            Status.ALREADY_EXISTS
            .withDescription(e.message)
            .withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixExistenteException
    }
}
