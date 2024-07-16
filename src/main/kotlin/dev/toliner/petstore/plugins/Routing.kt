package dev.toliner.petstore.plugins

import dev.toliner.petstore.route.routePet
import dev.toliner.petstore.route.routeUser
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
    }
    install(StatusPages) {
        @Serializable
        data class Error(val code: Int, val message: String)

        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = Error(HttpStatusCode.InternalServerError.value, cause.message ?: "Internal Server Error")
            )
        }
    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        routeUser()
        routePet()
    }
}

private val wrongIdResponse = setOf(
    "ざっこ〜♥", "よっわ♥", "むりむり♥", "だっさ〜♥", "カッコわる〜♥"
)

@Suppress("UnusedReceiverParameter")
fun PipelineContext<*, ApplicationCall>.getWrongIdResponse() = wrongIdResponse.random()

suspend fun PipelineContext<*, ApplicationCall>.respondNotFound() {
    call.respond(HttpStatusCode.NotFound, getWrongIdResponse())
}
