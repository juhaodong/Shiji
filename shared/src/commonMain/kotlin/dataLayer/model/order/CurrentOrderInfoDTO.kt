package dataLayer.model.order


import dataLayer.model.restaurant.CallServiceLog
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
data class LocalOrderInfo(
    val adultCount: Int = 0,
    val childCount: Int = 0,
    val tableId: Int = -1,
    val tableName: String = "-",
    val orderId: Int = -1,
    val consumeTypeId: Int = -1,
    val consumeTypeStatusId: Int = -1,
    val tableUsageStatus: Int = -1,
    val servantName: String = "",
    val addressInfo: AddressInfo? = null,
    val currentCallServiceLog: CallServiceLog? = null,
    val sourceMarks: List<String?> = emptyList()
) {
    fun tableIsActive(): Boolean {
        return tableId != -1 && tableUsageStatus == 1
    }
}

fun notOpenOrderInfo(tableId: Int, tableName: String): LocalOrderInfo {
    return LocalOrderInfo(tableId = tableId, tableName = tableName, tableUsageStatus = 0)
}

fun newTakeawayOrderInfo(): LocalOrderInfo {
    return LocalOrderInfo(tableId = -2, tableName = "Togo", tableUsageStatus = 0, consumeTypeId = 2)
}

val NoOrder = LocalOrderInfo(tableUsageStatus = 0)
val NetworkDown = LocalOrderInfo(tableUsageStatus = -1)

private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

@Serializable
data class CurrentOrderInfoDTO(
    val order: TableOrderInfoDTO,
    val servant: String,
    val adultCount: Int,
    val childCount: Int,
    val consumeTypeId: Int,
    val createTimeStamp: String,
    val firstRoundTime: String,
    val tableBasicInfo: TableBasicInfoDTO,
    val sourceMarks: List<String?>,
    val currentCallServiceLog: CallServiceLog? = null
) {
    fun toOrderInfo(): LocalOrderInfo {
        return LocalOrderInfo(
            tableId = order.tableId,
            tableName = tableBasicInfo.name,
            adultCount = adultCount,
            childCount = childCount,
            orderId = order.id,
            consumeTypeId = consumeTypeId,
            tableUsageStatus = tableBasicInfo.usageStatus,
            servantName = servant,
            consumeTypeStatusId = order.consumeTypeStatusId,
            sourceMarks = sourceMarks,
            currentCallServiceLog = currentCallServiceLog,
            addressInfo = order.rawAddressInfo?.let {
                try {
                    json.decodeFromString<AddressInfo>(string = it)

                } catch (e: Exception) {
                    null
                }
            }
        )
    }
}

@Serializable
data class TableOrderInfoDTO(
    val id: Int,
    val tableId: Int,
    val consumeTypeStatusId: Int,
    val rawAddressInfo: String? = null,
    @SerialName("metadata")
    val metaData: String? = null
)

@Serializable
data class OrderInfoDTO(
    val tableId: Int,
    val tableName: String,
    val orderId: Int
)

@Serializable
data class TableBasicInfoDTO(
    val lastRoundAt: String?,
    val name: String, val buffetRound: Int, val usageStatus: Int
)

@Serializable
data class AddressInfo(
    val firstName: String = "",
    val lastName: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val email: String = "",
    val tel: String = "",
    val oldTime: String = "",
    val note: String = "",
    val date: String = "",
    val time: String = "",
    val plz: String = "",
    val city: String = "",
    val deliveryMethod: String = "",
    val paid: Boolean = false
)
