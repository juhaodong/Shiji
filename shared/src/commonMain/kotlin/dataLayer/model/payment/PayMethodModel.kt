package dataLayer.model.payment

import kotlinx.serialization.Serializable

@Serializable
data class PayMethodModel(var id: Int, val name: String)