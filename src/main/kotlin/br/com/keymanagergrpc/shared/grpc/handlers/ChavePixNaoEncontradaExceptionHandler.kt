package br.com.keymanagergrpc.shared.grpc.handlers

import br.com.keymanagergrpc.pix.ChavePixNaoEncontradaException
import br.com.keymanagergrpc.shared.grpc.ExceptionHandler
import br.com.keymanagergrpc.shared.grpc.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChavePixNaoEncontradaExceptionHandler : ExceptionHandler<ChavePixNaoEncontradaException> {

    override fun handle(e: ChavePixNaoEncontradaException): StatusWithDetails {
        return StatusWithDetails(
            Status.NOT_FOUND
            .withDescription(e.message)
            .withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixNaoEncontradaException
    }
}
