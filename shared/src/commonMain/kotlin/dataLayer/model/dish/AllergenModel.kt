package dataLayer.model.dish

import kotlinx.serialization.Serializable

@Serializable
data class AllergenModel(
    val id: Int,
    val name: String,
    val displayText: String,
    val icon: String
)
