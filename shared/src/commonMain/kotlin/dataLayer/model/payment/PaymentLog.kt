package dataLayer.model.payment

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.Serializable
import modules.utils.BigDecimalSerializer

@Serializable
data class PaymentLog(
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val methodName: String,
    val id: Int,
    var uid: String? = null,
    val memberCardId: String? = null
)