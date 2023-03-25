package com.lotholl.polarnylis

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.slf4j.LoggerFactory

fun main() {
    val LOGGER = LoggerFactory.getLogger("MAIN")
    embeddedServer(Netty, port = 8001) {
        install(WebSockets) {

        }
        routing {
            webSocket("/*") {
                LOGGER.debug("New connection")
                val uri = call.request.uri
                SocketProxy(this@webSocket).start(uri)
                closeExceptionally(IllegalStateException())
                throw IllegalStateException()
            }
        }

    }.start(wait = true)
}
