package dataLayer.model

import kotlinx.serialization.Serializable

@Serializable
data class SendDTO(
    val mailTo: String,
    val uuid: String,
    val templateId: String = "z3m5jgr8xpoldpyo"
)