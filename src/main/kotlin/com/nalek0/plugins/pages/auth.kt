package com.nalek0.plugins.pages

import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

import com.nalek0.utils.*
import com.nalek0.sessions.*
import com.nalek0.plugins.classes.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun getClientUser(session: UserSession?): User? {
    return transaction {
        if (session == null) {
            null
        } else {
            val userRaw = UserDB
                .select { UserDB.token eq session.token }
                .firstOrNull()
            if (userRaw == null) {
                null
            } else {
                User.getFromRaw(userRaw)
            }
        }
    }
}

fun Application.configureAuthentication() {
    routing {
        post("/process-login") {
            val parameters = call.receiveParameters()
            val nickname = parameters["nickname"]
                ?: return@post call.respondRedirect("/history?makePopup&popupText=Wrong%20form%20input")
            val password = parameters["password"]
                ?: return@post call.respondRedirect("/history?makePopup&popupText=Wrong%20form%20input")

            val user = User.getFromNickname(nickname)
            if (user != null && user.hashedPassword == hash(password)) {
                call.sessions.set(UserSession(generateRandomString(10), user.token))
                call.respondRedirect(user.profileURL)
            } else {
                call.respondRedirect("/login?makePopup&popupText=User%20not%20found")
            }
        }

        get("/login") {
            val clientUser = getClientUser(call.sessions.get<UserSession>())
            if (clientUser == null) {
                call.respond(FreeMarkerContent("login.html", mapOf(
                    "makePopup" to (call.parameters["makePopup"] != null),
                    "popupText" to escape(call.parameters["popupText"])
                )))
            }
            else {
                call.respondRedirect(clientUser.profileURL)
            }
        }

        post("/process-register") {
            fun validNickname(nickname: String): Boolean {
                return "\\S{1,20}".toRegex().matches(nickname)
            }
            fun validPassword(password: String): Boolean {
                return "\\S{1,20}".toRegex().matches(password)
            }

            val parameters = call.receiveParameters()
            val nickname = parameters["nickname"]
                ?: return@post call.respondText("'nickname' must be set", status = HttpStatusCode.BadRequest)
            val password = parameters["password"]
                ?: return@post call.respondText("'password' must be set", status = HttpStatusCode.BadRequest)

            if (validNickname(nickname) && validPassword(password)) {
                if (User.getFromNickname(nickname) == null) {
                    val newUser = User.create(nickname, password)
                    call.sessions.set(UserSession(generateRandomString(10), newUser.token))
                    call.respondRedirect("/register?makePopup&popupText=OK")
                } else {
                    call.respondRedirect("/register?makePopup&popupText=Nickname%20is%20already%20used")
                }
            }
            else {
                call.respondRedirect("/register?makePopup&popupText=Not%20valid%20parameters")
            }
        }

        get("/register") {
            val clientUser = getClientUser(call.sessions.get<UserSession>())
            if (clientUser == null) {
                call.respond(FreeMarkerContent("register.html", mapOf(
                    "makePopup" to (call.parameters["makePopup"] != null),
                    "popupText" to escape(call.parameters["popupText"])
                )))
            }
            else {
                call.respondRedirect(clientUser.profileURL)
            }
        }
    }
}