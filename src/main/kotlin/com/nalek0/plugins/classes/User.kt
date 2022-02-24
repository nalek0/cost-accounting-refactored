package com.nalek0.plugins.classes

import com.nalek0.utils.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

data class User(
    private val userId: Int,
    val nickname: String,
    val hashedPassword: String,
    val token: String,
    val wallets: MutableList<Wallet>,
    val avatarURL: String = "/static/images/default-avatar.jpg"
) {
    val profileURL: String
        get() = "/"

    override fun toString(): String {
        return "User($nickname)"
    }

    companion object {
        fun create(newNickname: String, newPassword: String): User {
            return transaction {
                val userId = UserDB.insertAndGetId { newUser ->
                    newUser[nickname] = newNickname
                    newUser[hashedPassword] = hash(newPassword)
                    newUser[token] = generateRandomString(50)
                }
                val userRaw = UserDB.select { UserDB.id eq userId }.first()

                commit()

                getFromRaw(userRaw)
            }
        }

        fun getFromId(id: Int): User? {
            return transaction {
                val userRaw = UserDB
                    .select { UserDB.id eq id }
                    .firstOrNull()

                commit()

                if (userRaw == null) {
                    null
                } else {
                    getFromRaw(userRaw)
                }
            }
        }

        fun getFromNickname(nickname: String): User? {
            return transaction {
                val userRaw = UserDB
                    .select { UserDB.nickname eq nickname }
                    .firstOrNull()

                commit()

                if (userRaw == null) {
                    null
                } else {
                    getFromRaw(userRaw)
                }
            }
        }

        fun getFromRaw(userRaw: ResultRow): User {
            return User(
                userRaw[UserDB.id].value,
                userRaw[UserDB.nickname],
                userRaw[UserDB.hashedPassword],
                userRaw[UserDB.token],
                userRaw[UserDB.wallets]
                    .split(' ')
                    .filter { it.trimIndent() != "" }
                    .map { Wallet.getFromId(it.toInt()) }
                    .toMutableList()
            )
        }
    }

    fun commit() {
        transaction {
            UserDB.update({ UserDB.id eq userId }) {
                it[this.nickname] = this@User.nickname
                it[this.wallets] = this@User.wallets
                    .joinToString(" ") { wallet -> wallet.walletId.toString() }
            }
        }
    }
}
