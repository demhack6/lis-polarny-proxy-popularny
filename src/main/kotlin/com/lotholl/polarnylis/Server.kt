package com.lotholl.polarnylis

import com.lotholl.polarnylis.remote.RemoteDao
import com.lotholl.polarnylis.remote.RemoteDaoImpl
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
    val config = Config.load()
    val remoteDao: RemoteDao = RemoteDaoImpl(config)

    embeddedServer(Netty, port = 8001) {
        install(WebSockets) {

        }
        routing {
            webSocket("/*") {
                LOGGER.debug("New connection")
                val uri = call.request.uri.removePrefix("/")
                SocketProxy(remoteDao, this@webSocket).start(uri)
                closeExceptionally(IllegalStateException())
                throw IllegalStateException()
            }
        }

    }.start(wait = true)
}
