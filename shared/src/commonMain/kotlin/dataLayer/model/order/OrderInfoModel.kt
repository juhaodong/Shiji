package dataLayer.model.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderInfoModel(
    val electronicUuid: String?,
    val tableId: Int,
    val consumeTypeStatusId: Int,
    val consumeTypeId: Int
)

@Serializable
data class OrderSubmitResult(
    val success: Boolean,
    val orderId: Int? = null,
    val message: String = ""
)