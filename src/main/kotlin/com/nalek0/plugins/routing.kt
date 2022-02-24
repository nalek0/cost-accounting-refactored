package com.nalek0.plugins

import com.nalek0.plugins.pages.*
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*

fun Application.configureRouting() {
    configureStatic()
    configureAuthentication()
    configureMainPage()
    configureWalletsPage()
}
