package com.lotholl.polarnylis

import com.lotholl.polarnylis.remote.RemoteDao
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.*

class SocketProxy(private val remoteDao: RemoteDao, private val reqSession: DefaultWebSocketServerSession) {

    val LOGGER = LoggerFactory.getLogger(javaClass)
    private val client = HttpClient(CIO) {
        install(io.ktor.client.plugins.websocket.WebSockets)
    }


    fun start(uri: String) {
        val (host, port) = loadTargetAddress(uri)

        runBlocking {
            client.webSocket(method = HttpMethod.Get, host = host, port = port, path = REMOTE_URI) {
                val remote = launch { remoteToClient(reqSession, this@webSocket) }
                val requesting = launch { clientToRemote(reqSession, this@webSocket) }

                remote.join()
                requesting.cancelAndJoin()
            }
        }
        client.close()
    }

    private suspend fun remoteToClient(
        reqSession: DefaultWebSocketServerSession,
        srvSession: DefaultClientWebSocketSession
    ) {
        try {
            while (true) {
                for (frame in srvSession.incoming) {
                    LOGGER.debug("SRV: ${frame.data.decodeToString()}")
                    reqSession.send(frame)
                }
            }
        } catch (e: Throwable) {
            LOGGER.debug("Error sending remote to client")
        }
    }

    private suspend fun clientToRemote(
        reqSession: DefaultWebSocketServerSession,
        srvSession: DefaultClientWebSocketSession
    ) {
        try {
            while (true) {
                for (frame in reqSession.incoming) {
                    LOGGER.debug("REQ: ${frame.data.decodeToString()}")
                    srvSession.send(frame)
                }
            }
        } catch (e: Throwable) {
            LOGGER.debug("Error forwarding client to remote: $e")
        }
    }

    private fun loadTargetAddress(uuidString: String): Pair<String, Int> =
        remoteDao.getAddress(UUID.fromString(uuidString))

    companion object {
        const val REMOTE_URI = "/ws?password=neko"
    }

}
