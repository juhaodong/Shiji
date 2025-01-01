package dataLayer.model

import kotlinx.serialization.Serializable

@Serializable
data class SourceMark(
    val id: String,
    val code: String,
)