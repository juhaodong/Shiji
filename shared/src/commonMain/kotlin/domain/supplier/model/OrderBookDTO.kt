package domain.supplier.model

import kotlinx.serialization.Serializable

@Serializable
class OrderBookDTO(
    val displayName: String,
    val customerReference: String,
    val ready: Boolean = true,
    val id: Long,
)

@Serializable
class OrderBookEditModel(
    val displayName: String,
    val customerReference: String
)