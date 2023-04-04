package com.jww.grpcpro

import android.net.Uri
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.io.Closeable

class GRPCTestClass(uri: Uri) : Closeable {
    private val channel = let {
        println("Connecting to ${uri.host}:${uri.port}")

        val builder = ManagedChannelBuilder.forAddress(uri.host, uri.port)
        if (uri.scheme == "https") {
            builder
                .useTransportSecurity()
        } else {
            builder.usePlaintext()
        }

        builder.executor(Dispatchers.IO.asExecutor()).build()
    }

    private val greeter = GreeterGrpcKt.GreeterCoroutineStub(channel)

    suspend fun sayHello(name: String) {
        kotlin.runCatching {
            val request = helloRequest { this.name = name }
            greeter.sayHello(request)
        }.onSuccess { response ->
            print(response.message)
        }.onFailure { e ->
            e.printStackTrace()
        }
    }

    override fun close() {
        channel.shutdownNow()
    }
}