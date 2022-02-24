package com.nalek0.plugins.pages

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*

import com.nalek0.plugins.classes.*

fun Application.configureStatic() {
    routing {
        static("/static") {
            resources("static")
        }
    }
}