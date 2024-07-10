package dev.toliner.petstore.route

import dev.toliner.petstore.model.User
import dev.toliner.petstore.plugins.getWrondIdResponse
import dev.toliner.petstore.plugins.respondNotFound
import dev.toliner.petstore.repository.UserRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Routing.routeUser() = route("user") {
    val repository by inject<UserRepository>()

    post {
        @Serializable
        data class Request(val username: String, val type: User.Type)

        val request = call.receive<Request>()

        if (repository.findByName(request.username) != null) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        val user = repository.createUser(request.username, request.type)
        call.respond(HttpStatusCode.Created, user)
    }

    route("/{userId}") {
        get {
            val userId = call.parameters["userId"]?.toLongOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.NotFound, getWrondIdResponse())
                return@get
            }
            val user = repository.findById(userId)
            if (user != null) {
                call.respond(user)
            } else {
                call.respond(HttpStatusCode.NotFound, getWrondIdResponse())
            }
        }

        put {
            @Serializable
            data class Request(val username: String)

            val userId = call.parameters["userId"]?.toLongOrNull()
            if (userId == null) {
                respondNotFound()
                return@put
            }
            val request = call.receive<Request>()
            val user = repository.findById(userId)
            if (user == null) {
                respondNotFound()
                return@put
            }
            if (repository.findByName(request.username) != null) {
                call.respond(HttpStatusCode.Conflict)
                return@put
            }
            val result = repository.updateUserName(userId, request.username)
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                respondNotFound()
            }
        }
    }
}

