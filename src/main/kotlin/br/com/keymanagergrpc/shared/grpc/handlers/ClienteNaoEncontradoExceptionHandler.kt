package br.com.keymanagergrpc.shared.grpc.handlers

import br.com.keymanagergrpc.pix.ChavePixExistenteException
import br.com.keymanagergrpc.pix.ClienteNaoEncontradoException
import br.com.keymanagergrpc.shared.grpc.ExceptionHandler
import br.com.keymanagergrpc.shared.grpc.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ClienteNaoEncontradoExceptionHandler : ExceptionHandler<ClienteNaoEncontradoException> {

    override fun handle(e: ClienteNaoEncontradoException): StatusWithDetails {
        return StatusWithDetails(
            Status.NOT_FOUND
            .withDescription(e.message)
            .withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ClienteNaoEncontradoException
    }
}
