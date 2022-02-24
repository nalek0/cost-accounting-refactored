package com.nalek0

import com.nalek0.plugins.classes.*
import com.nalek0.utils.generateRandomString
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest

fun main() {
    connectToDatabase()

    transaction {
        addLogger(StdOutSqlLogger)

        UserDB.deleteAll()
        WalletDB.deleteAll()
        OperationDB.deleteAll()
    }
}