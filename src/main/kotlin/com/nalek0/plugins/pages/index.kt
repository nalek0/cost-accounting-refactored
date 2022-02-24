package com.nalek0.plugins.pages

import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

import com.nalek0.plugins.classes.*
import com.nalek0.sessions.*

fun Application.configureMainPage() {
    routing {
        get("/") {
            val clientUser = getClientUser(call.sessions.get<UserSession>())
            if (clientUser != null) {
                call.respond(FreeMarkerContent("index.html", mapOf<String, Any>(
                    "clientUser" to clientUser
                )))
            }
            else {
                call.respondRedirect("/login")
            }
        }
    }
}