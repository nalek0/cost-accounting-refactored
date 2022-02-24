package com.nalek0.plugins.classes

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object UserDB: IntIdTable() {
    val nickname: Column<String> = varchar("nickname", 50).uniqueIndex()
    val hashedPassword: Column<String> = varchar("hashed_password", 100)
    val token: Column<String> = varchar("token", 50).uniqueIndex()
    val wallets: Column<String> = text("wallets").default("")
}

object WalletDB: IntIdTable() {
    val name: Column<String> = varchar("name", 50).uniqueIndex()
    val walletURLId: Column<String> = varchar("wallet_url_id", 6).uniqueIndex()
    val operations: Column<String> = text("operations").default("")
}

object OperationDB: IntIdTable() {
    val deltaAmount: Column<Int> = integer("delta_amount")
    val date: Column<Long> = long("date")
}