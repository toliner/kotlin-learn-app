package dev.toliner.petstore

import dev.toliner.petstore.plugins.configureDatabases
import dev.toliner.petstore.plugins.configureMonitoring
import dev.toliner.petstore.plugins.configureRouting
import dev.toliner.petstore.plugins.configureSerialization
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(configureDatabases())
    }
    configureMonitoring()
    configureSerialization()
    configureRouting()
}
