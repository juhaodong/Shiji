package dataLayer.model.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


object BuffetSetting {
    const val NoLimit = -1
}

@Serializable
data class BuffetSettingModel(
    val consumeTypeId: Int,
    var totalRound: Int = BuffetSetting.NoLimit,
    var roundDishCount: Int = 5,
    var childRoundDishCount: Int = 5,
    @SerialName("maxDineTime")
    var maxDineTimeSecond: Int = BuffetSetting.NoLimit,
    @SerialName("roundTime")
    var roundTimeSecond: Int = 600,
)