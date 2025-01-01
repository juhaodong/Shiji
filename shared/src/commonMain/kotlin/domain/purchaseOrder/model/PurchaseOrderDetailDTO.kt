package domain.purchaseOrder.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import domain.inventory.model.OrderStatus
import domain.supplier.model.OrderBook
import domain.supplier.model.ShopInfo
import domain.supplier.model.SupplierSetting
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
class PurchaseOrderDetailDTO(
    val purchaseOrder: PurchaseOrder,
    val orderContents: List<OrderItem>,
    val orderBook: OrderBook,
    val orderChangeLog: List<OrderChangeLog>,
    val supplier: SupplierSetting,
    val shop: ShopInfo
)

@Serializable
class OrderItem(
    val name: String,
    val description: String,
    val imageUrl: String,
    val orderBookProductId: Long?,
    @Contextual
    var amount: BigDecimal,
    @Contextual
    var price: BigDecimal,
    @Contextual
    var taxRate: BigDecimal,
    @Contextual
    var afterTaxPrice: BigDecimal,
    val purchaseOrderId: Long,
    val purchaseUnit: String
)

@Serializable
class OrderChangeLog(
    var previousStatus: OrderStatus,
    var status: OrderStatus,
    val estimateArriveDateTime: LocalDateTime,
    var note: String,
    var operator: String,
    var imageUrl: String,
    val orderId: Long,
    val orderUUID: String,
    val records: List<OrderChangeLogRecord> = listOf(),
    val createTimestamp: LocalDateTime
)

@Serializable
class OrderChangeLogRecord(
    val name: String,
    val oldAmount: String,
    val newAmount: String,
)


class TransformInfo(
    val name: String,
    val unit: String,
    val amount: BigDecimal,
)