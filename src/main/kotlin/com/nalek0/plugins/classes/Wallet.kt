package com.nalek0.plugins.classes

import com.nalek0.utils.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

data class Wallet(
    val walletId: Int,
    val name: String,
    val walletStringId: String,
    val operations: MutableList<Operation>
) {
    val amountCurrency: String = "₽"
    val walletURL: String
        get() = "/wallet/$walletStringId"

    val totalAmount: Int
        get() = operations.sumOf { it.deltaAmount }

    fun addOperation(operation: Operation) {
        operations.add(operation)
    }

    fun getLastOperations(number: Int) =
        operations
            .slice((0.coerceAtLeast(operations.size - number)) until operations.size)
            .reversed()

    fun getAllLastOperations() =
        operations.reversed()

    fun sizeOfOperations() =
        operations.size
    
    companion object {
        fun create(newName: String): Wallet {
            return transaction {
                val walletId = WalletDB.insertAndGetId { newWallet ->
                    newWallet[name] = newName
                    newWallet[walletURLId] = generateSymbolicString(6)
                }
                val walletRaw = WalletDB.select { WalletDB.id eq walletId }.first()

                commit()

                getFromRaw(walletRaw)
            }
        }

        fun getFromId(id: Int): Wallet {
            return transaction {
                val walletRaw = WalletDB.select { WalletDB.id eq id }.first()

                commit()

                getFromRaw(walletRaw)
            }
        }

        fun getFromRaw(walletRaw: ResultRow): Wallet {
            return Wallet(
                walletId = walletRaw[WalletDB.id].value,
                name = walletRaw[WalletDB.name],
                walletStringId = walletRaw[WalletDB.walletURLId],
                operations = walletRaw[WalletDB.operations]
                    .split(' ')
                    .filter { it.trimIndent() != "" }
                    .map { Operation.getFromId(it.toInt()) }
                    .toMutableList()
            )
        }
    }

    fun commit() {
        transaction {
            WalletDB.update({ WalletDB.id eq walletId }) {
                it[this.operations] = this@Wallet.operations
                    .joinToString(" ") { operation -> operation.operationId.toString() }
            }
        }
    }
}
