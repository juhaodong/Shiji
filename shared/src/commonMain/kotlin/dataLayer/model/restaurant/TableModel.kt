package dataLayer.model.restaurant

import dataLayer.model.order.AddressInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

enum class CallServiceReason {
    CallAccept,
    AddDishes,
    Others,
    CheckOut
}

@Serializable
data class CallServiceLog(
    val id: String,
    val orderId: String,
    val tableId: String,
    val callServiceReason: CallServiceReason,
    val raisedBy: String,
    val createdAt: String,
    val closedBy: String? = null,
    val closedAt: String? = null
)

@Serializable
data class TableModel(
    val tableId: Int,
    val tableName: String,
    val usageStatus: Int,
    val sectionId: Int,
    val callService: Int,
    val servantName: String?,
    val dishCount: Int,
    val drinkCount: Int,
    val buffetCount: Int,
    val totalPrice: Double?,
    val consumeType: Int?,
    val consumeTypeStatusId: Int?,
    val createTimestamp: String?,
    val rawAddressInfo: String?,
    val currentCallServiceLog: CallServiceLog?
) {
    var addressInfo: AddressInfo? = null
    fun addressInfo(): AddressInfo? {
        if (rawAddressInfo != null) {
            if (addressInfo == null) {
                addressInfo = json.decodeFromString(rawAddressInfo)
            }
            return addressInfo
        }
        return null
    }
}


@Serializable
data class SimpleTableModel(
    val id: Int,
    val name: String,
    val usageStatus: Int
)
