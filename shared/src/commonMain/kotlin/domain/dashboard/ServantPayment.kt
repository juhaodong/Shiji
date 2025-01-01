package domain.dashboard

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import modules.utils.BigDecimalSerializer

@Serializable
class ServantPayment(
    val deviceId: String,
    val date: LocalDate,
    val lastUpdate: LocalDateTime,
    val servantId: Int,
    val name: String,
    var orderCount: Int,
    @Serializable(with = BigDecimalSerializer::class)
    var total: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    var tip: BigDecimal,
)