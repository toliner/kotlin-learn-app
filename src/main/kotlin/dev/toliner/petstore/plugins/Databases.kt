package dev.toliner.petstore.plugins

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.koin.core.module.Module
import org.koin.dsl.module

fun Application.configureDatabases() : Module {
    val dbConfig = environment.config.config("db")
    val database = Database.connect(
        url = dbConfig.property("url").getString(),
        user = dbConfig.property("user").getString(),
        driver = dbConfig.property("driver").getString(),
        password = dbConfig.property("password").getString()
    )
    return module {
        single { database }
    }
}
