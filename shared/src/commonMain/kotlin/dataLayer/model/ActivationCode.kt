package dataLayer.model

import kotlinx.serialization.Serializable

@Serializable
data class ActivationCode(
    val code: String,
    val deviceType: DeviceType,
    val baseIP: String,
    val bindUUID: String? = null,
    val deviceId: String? = null
)

enum class DeviceType {
    Mobile
}

