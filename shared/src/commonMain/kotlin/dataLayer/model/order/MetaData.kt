package dataLayer.model.order

import kotlinx.serialization.Serializable

@Serializable
data class MetaData(
    val version: String = "Compose/Pad",
    val markMetaInfoList: MutableList<MarkMetaInfo> = mutableListOf()
)
@Serializable
data class MarkMetaInfo(
    val sourceMark: String?,
    var lastRoundAt: String? = null,
    var firstRoundAt: String? = null,
    var currentRound: Int = 1
)
