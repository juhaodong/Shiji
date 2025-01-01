package domain.purchaseOrder.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.raedghazal.kotlinx_datetime_ext.now
import domain.inventory.model.OrderStatus
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
class PurchaseOrder(
    val id: Long,
    var estimateArriveDateTime: LocalDateTime,
    var status: OrderStatus = OrderStatus.Created,
    val lastUpdate: LocalDateTime = LocalDateTime.now(),
    var completeTime: LocalDateTime? = null,
    @Contextual
    var totalPrice: BigDecimal = BigDecimal.ZERO,
    var note: String = "",
    val shopId: Long,
    val supplierId: Long,

    val supplierName: String,

    val supplierImageUrl: String,
    val billUrl: String? = null,
    var signedBy: String = "",
    var signImageUrl: String = "",
    val orderUUID: String,
    val createTimestamp: LocalDateTime,
) {
    fun getDisplayId(): String {
        return orderUUID
    }
}