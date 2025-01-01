package dataLayer.model

import kotlinx.serialization.Serializable

@Serializable
data class ServantModel(
    val name: String,
    val password: String,
    val isPartTime: Int,
    val permission: Int,
    val rawAuth: String,
)
