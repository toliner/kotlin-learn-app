package dev.toliner.petstore.repository

import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.module.Module
import org.koin.dsl.bind
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
        single { UserRepositoryImpl(get()) }.bind(UserRepository::class)
        single { PetRepositoryImpl(get()) }.bind(PetRepository::class)
    }
}

class NameConflictException internal constructor() : RuntimeException()

internal suspend fun <T> Database.query(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO, this) { block() }
