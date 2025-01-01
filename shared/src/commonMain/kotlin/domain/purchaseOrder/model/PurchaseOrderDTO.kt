package domain.purchaseOrder.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


@Serializable
data class OrderItemDTO(
    val orderBookProductId: Long,
    @Contextual
    val amount: BigDecimal,
)


@Serializable
data class PurchaseOrderDTO(
    val orderList: List<OrderItemDTO>,
    val estimateArriveDateTime: LocalDateTime,
    val note: String,
    val orderBookId: Long
)