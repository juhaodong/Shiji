package domain.dashboard

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import modules.utils.BigDecimalSerializer

@Serializable
class PaymentInfo(
    val deviceId: String,
    val date: LocalDate,
    val lastUpdate: LocalDateTime,
    val payMethodId: Int,
    val name: String,
    var count: Int,
    @Serializable(with = BigDecimalSerializer::class)
    var total: BigDecimal,
)