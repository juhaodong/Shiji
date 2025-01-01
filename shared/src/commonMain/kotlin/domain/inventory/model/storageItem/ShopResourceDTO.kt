@file:OptIn(ExperimentalUuidApi::class)

package domain.inventory.model.storageItem

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ShopResourceDTO(
    val id: Long? = null,
    val shopId: Long,
    val parentId: Long,
    val name: String,
    val imageUrl: String = "",
    @Contextual
    val maxUnitPrice: BigDecimal,
    val inventoryPeriodDays: Int,
    val primarySku: String,
    val unitList: List<UnitDTO>,
)

@Serializable
data class UnitDTO(
    val name: String = "",
    @Contextual
    val nextLevelFactor: BigDecimal
)

@Serializable
data class ShopResourceEditModel(
    val name: String,
    val parentId: Long,
    @Contextual
    val maxUnitPrice: BigDecimal,
    val unitList: List<UnitDTO>,
    val inventoryPeriodDays: Int,
    val sku: String
)

fun ShopResourceEditModel.toDTO(
    shopId: Long,
    imageUrl: String = "",
    id: Long? = null
): ShopResourceDTO {
    return ShopResourceDTO(
        id = id,
        shopId = shopId,
        parentId = parentId,
        name = name,
        imageUrl = imageUrl,
        maxUnitPrice = maxUnitPrice,
        inventoryPeriodDays = inventoryPeriodDays,
        primarySku = sku.ifBlank { Uuid.random().toString() },
        unitList = unitList
    )
}

@Serializable
class PurchasePriceDTO(
    val id: Long?,
    val dishResourceId: Long,
    @Contextual
    val taxRate: BigDecimal,
    @Contextual
    val salePrice: BigDecimal,
    val sku: String,
    val supplierId: Long? = null,
)