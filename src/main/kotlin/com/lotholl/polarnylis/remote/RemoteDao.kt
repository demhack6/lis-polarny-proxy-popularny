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
        val raw = jedis.get("b4a1cff7-63a1-43c2-b5a0-9a3f05451df5")
        val (host, port) = raw.split(":")
        return host to port.toInt()
    }

}