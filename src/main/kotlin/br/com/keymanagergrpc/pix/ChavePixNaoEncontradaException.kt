package br.com.keymanagergrpc.pix

import java.lang.RuntimeException

class ChavePixNaoEncontradaException(message: String?) : RuntimeException(message)
