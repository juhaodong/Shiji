package domain.inventory.model.storageItem

import kotlinx.serialization.Serializable

@Serializable
class ResourceCategoryModel(
    val name: String,
    val icon: String = "",
    val color: String = "",
    val shopId: Long,
    val id: Long? = null
)