package dataLayer.model.oldOrder

import kotlinx.serialization.Serializable

@Serializable
data class OldOrderDTO(
    val orders: List<OldOrder>,
    val payMethodTotal: List<OrderPaymentInfo>,
    val todayTotal: Double,
    val tipIncome: Double? = 0.0
)

@Serializable
data class OrderPaymentInfo(
    val payMethodId: Int,
    val sumTotal: Double,
    val paymentMethodStrings: String
)

@Serializable
data class OldOrder(
    val backGroundColor: String,
    val discountStr: String,
    val foreGroundColor: String,
    val orderId: Int,
    val payMethodId: Int,
    val consumeTypeStatusId: Int,
    val paymentMethodStrings: String,
    val paymentLabel: String?,
    val tableName: String,
    val tipIncome: Double,
    val totalPrice: Double,
    val updatedAt: String,
)

fun String.toColorInt(): Int {
    if (this[0] == '#') {
        var color = substring(1).toLong(16)
        if (length == 7) {
            color = color or 0x00000000ff000000L
        } else if (length != 9) {
            return 0xffffff
        }
        return color.toInt()
    }
    return 0xffffff
}