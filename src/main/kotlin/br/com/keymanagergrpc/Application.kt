package br.com.keymanagergrpc

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.keymanagergrpc")
		.start()
}

