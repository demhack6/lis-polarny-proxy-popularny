package com.lotholl.polarnylis

data class Config(
    val REDIS_HOST: String,
    val REDIS_PORT: Int,
) {
    companion object {
        fun load(): Config {
            val host = System.getenv()["REDIS_HOST"]
            val port = System.getenv()["REDIS_PORT"]

            requireNotNull(host)
            requireNotNull(port)

            return Config(host, port.toInt())
        }
    }
}