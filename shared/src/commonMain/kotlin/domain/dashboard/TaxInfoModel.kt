package domain.dashboard

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import modules.utils.BigDecimalSerializer

@Serializable
class TaxInfoModel(
    val deviceId: String,
    val date: LocalDate,
    val lastUpdate: LocalDateTime,
    @Serializable(with = BigDecimalSerializer::class)
    val taxRate: BigDecimal,
    val taxName: String,
    @Serializable(with = BigDecimalSerializer::class)
    var tax: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    var total: BigDecimal,
)