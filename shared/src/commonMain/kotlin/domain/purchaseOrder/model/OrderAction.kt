package domain.purchaseOrder.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

enum class OrderAction {
    Confirm,
    Continue,
    Hold,
    ToDelivery,
    Archive,
    Cancel,
    Reject,
    Edit,
}

@Serializable
class ProductOrderSignDTO(
    val orderId: Long,
    val imageUrl: String = "",
    val note: String,
    val signedBy: String,
    val itemAmountCheckDesc: Map<Long, @Contextual BigDecimal>? = null,
)

@Serializable
open class OrderChangeBasicDTO(
    val note: String = "",
    val imageUrl: String = "",
    val orderId: Long,
    val estimateArriveDateTime: LocalDateTime? = null,
    val itemAmountCheckDesc: Map<Long, @Contextual BigDecimal>? = null,
)
