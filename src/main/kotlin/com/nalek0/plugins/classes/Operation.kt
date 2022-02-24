package com.nalek0.plugins.classes

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.TextStyle
import java.util.*

data class Operation(
    val operationId: Int,
    val deltaAmount: Int,
    val date: Long
) {
    private val localDateTime: LocalDateTime
        = Instant.fromEpochMilliseconds(date).toLocalDateTime(TimeZone.currentSystemDefault())
    val stringDate: String
        = "${localDateTime.dayOfMonth} " +
            "${localDateTime.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} " +
            "${localDateTime.year}"

    companion object {
        fun create(newDeltaAmount: Int): Operation {
            return transaction {
                val operationId = OperationDB.insertAndGetId { newOperation ->
                    newOperation[deltaAmount] = newDeltaAmount
                    newOperation[date] = System.currentTimeMillis()
                }
                val operationRaw = OperationDB.select { OperationDB.id eq operationId }.first()

                commit()

                getFromRaw(operationRaw)
            }
        }

        fun getFromId(id: Int): Operation {
            return transaction {
                val operationRaw = OperationDB.select { OperationDB.id eq id }.first()

                commit()

                getFromRaw(operationRaw)
            }
        }

        fun getFromRaw(operationRaw: ResultRow): Operation {
            return Operation(
                operationId = operationRaw[OperationDB.id].value,
                deltaAmount = operationRaw[OperationDB.deltaAmount],
                date = operationRaw[OperationDB.date]
            )
        }
    }
}