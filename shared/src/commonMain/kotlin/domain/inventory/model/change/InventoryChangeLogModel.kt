package domain.inventory.model.change

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import domain.inventory.model.storageItem.StorageItemModel
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

enum class OperationType {
    Enter,
    Out
}

enum class OperatorType {
    Cashier,
    Internal,
    Check,
    Loss,
    Produce,
    Sales
}

@Serializable
data class InventoryChangeLogModel(
    val id: Long,
    val shopId: Long,
    val storageItem: StorageItemModel,
    @Contextual
    var amount: BigDecimal,
    @Contextual
    val priceLiteral: BigDecimal,
    @Contextual
    val costLiteral: BigDecimal,
    val operationType: OperationType,
    val operatorType: OperatorType,
    var cancelled: Boolean = false,
    var unitDisplay: String,
    val note: String = "",
    var createTimestamp: LocalDateTime,
)