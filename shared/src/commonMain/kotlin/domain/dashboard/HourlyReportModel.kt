package domain.dashboard

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import modules.utils.BigDecimalSerializer

@Serializable
class HourlyReportModel(
    val deviceId: String,
    val date: LocalDate,
    val lastUpdate: LocalDateTime,
    val hour: Int,
    var closedCount: Int,
    var openCount: Int,
    var orderItemCount: Int,
    @Serializable(with = BigDecimalSerializer::class)
    var total: BigDecimal
) 