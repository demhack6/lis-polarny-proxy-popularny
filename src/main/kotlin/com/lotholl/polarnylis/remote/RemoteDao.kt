package com.lotholl.polarnylis.remote

import com.lotholl.polarnylis.Config
import redis.clients.jedis.JedisPooled
import java.util.UUID

interface RemoteDao {
    fun getAddress(id: UUID): Pair<String, Int>
}

class RemoteDaoImpl(config: Config): RemoteDao {
    val jedis = JedisPooled(config.REDIS_HOST, config.REDIS_PORT)

    override fun getAddress(id: UUID): Pair<String, Int> {
        val raw = jedis.get(id.toString())
        val (host, port) = raw.split(":")
        return host to port.toInt()
    }

}