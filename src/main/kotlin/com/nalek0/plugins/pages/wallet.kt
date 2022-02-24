package com.nalek0.plugins.pages

import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.request.*

import com.nalek0.sessions.*
import com.nalek0.plugins.classes.*
import com.nalek0.utils.*


fun Application.configureWalletsPage() {
    routing {
        post("/wallet-add") {
            val clientUser = getClientUser(call.sessions.get<UserSession>())
                ?: return@post call.respondRedirect("/login")
            val parameters = call.receiveParameters()
            val name = parameters["name"]
                ?: return@post call.respondRedirect("/wallets?makePopup&popupText=Wrong%20form%20format")
            val wallet = Wallet.create(name)
            clientUser.wallets.add(wallet)
            clientUser.commit()
            call.respondRedirect("/wallets?makePopup&popupText=OK")
        }
        get("/wallets") {
            val clientUser = getClientUser(call.sessions.get<UserSession>())
                ?: return@get call.respondRedirect("/login")
            call.respond(
                FreeMarkerContent("wallets.html", mapOf(
                    "clientUser" to clientUser,
                    "makePopup" to (call.parameters["makePopup"] != null),
                    "popupText" to escape(call.parameters["popupText"])
                ))
            )
        }

        post("/operation-add") {
            val clientUser = getClientUser(call.sessions.get<UserSession>())
                ?: return@post call.respondRedirect("/login")
            val parameters = call.receiveParameters()

            val walletStringId = parameters["id"]
                ?: return@post call.respondRedirect("/wallets?makePopup&popupText=Strange%20wallet%20id")
            val wallet = clientUser.wallets
                .find { it.walletStringId == walletStringId }
                ?: return@post call.respondRedirect("/wallets?makePopup&popupText=Strange%20wallet%20id")

            val valueSign = if (parameters["less-zero"] == "on") -1 else 1
            val valueInt = parameters["value"]
                ?.toIntOrNull()
                ?: return@post call.respondRedirect("/wallet/$walletStringId?makePopup&popupText=Strange%20value")

            val value = valueSign * valueInt
            wallet.addOperation(Operation.create(value))
            wallet.commit()
            call.respondRedirect("/wallet/$walletStringId?makePopup&popupText=OK")
        }
        get("/wallet/{id}") {
            val clientUser = getClientUser(call.sessions.get<UserSession>())
                ?: return@get call.respondRedirect("/login")
            val walletId = call.parameters["id"]
                ?: return@get call.respondRedirect("/wallets")
            val wallet = clientUser.wallets
                .find { it.walletStringId == walletId }
                ?: return@get call.respondRedirect("/wallets")
            call.respond(
                FreeMarkerContent("wallet.html", mapOf(
                    "clientUser" to clientUser,
                    "wallet" to wallet,
                    "makePopup" to (call.parameters["makePopup"] != null),
                    "popupText" to escape(call.parameters["popupText"])
                ))
            )
        }
    }
}