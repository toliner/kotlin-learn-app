package dev.toliner.petstore.route

import dev.toliner.petstore.model.Pet
import dev.toliner.petstore.repository.PetRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.routePet() = route("/pets") {
    val repository by inject<PetRepository>()
    post {
        @Serializable
        data class Request(val name: String, val tag: List<String>, val status: Pet.Status)

        val request = call.receive<Request>()
        val result = repository.createPet(request.name, request.tag, request.status)
        call.respond(HttpStatusCode.Created, result)
    }
}
