package com.nalek0

import com.nalek0.plugins.configureRouting
import com.nalek0.sessions.UserSession
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.sessions.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Serializable
data class DatabaseConnectionConfig(
    val user: String,
    val password: String,
    val hostname: String,
    val port: Int,
    val databaseName: String
)

fun connectToDatabase() {
    val config: DatabaseConnectionConfig =
        Json.decodeFromString(File("src/main/resources/database-config.json").readText())


    Database.connect("jdbc:postgresql://${config.hostname}:${config.port}/${config.databaseName}",
        driver = "org.postgresql.Driver",
        user = config.user, password = config.password)
}

fun Application.module() {
    connectToDatabase()

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Sessions) {
        cookie<UserSession>("token") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 86400
        }
    }

    configureRouting()
}