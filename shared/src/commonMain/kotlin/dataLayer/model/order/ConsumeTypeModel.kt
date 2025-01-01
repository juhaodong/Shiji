package dataLayer.model.order

import kotlinx.serialization.Serializable

@Serializable
data class ConsumeTypeModel(val id: Int, val name: String, val isHotpot: Int?) {
    fun isHotPotB(): Boolean {
        return isHotpot == 1
    }
}

@Serializable
data class RequiredCategoryModel(
    val consumeTypeId: Int,
    val categoryId: Int,
    val requireCount: Int
)
