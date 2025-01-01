package dataLayer.model.restaurant

import kotlinx.serialization.Serializable

@Serializable
data class FreeInformationModel(val id: Int, val name: String, val icon: String?)
