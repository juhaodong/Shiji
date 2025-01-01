package domain.supplier.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import domain.inventory.model.storageItem.getRealUrl
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
class OrderBookItemInfo(
    val id: Long,
    val orderBookId: Long,
    val product: SupplierProduct,
    val overrideName: String,
    val note: String,
    @Contextual
    val price: BigDecimal,
    var transFormTo: Long? = null,
    var transFormResourceName: String = "",
    @Contextual
    var transFormAmount: BigDecimal = BigDecimal.ONE,
    var transFormUnitDisplay: String = "",
    val orderBookCategory: OrderBookCategory
) {
    fun getRealName(): String {
        return overrideName.ifBlank { product.name }
    }
}


enum class VisibilityLevel {
    PUBLIC,
    PROTECTED,
    PRIVATE
}

@Serializable
class SupplierProduct(
    val id: Long,
    val supplierId: Long,
    var name: String,
    var description: String,
    var imageUrl: String,
    @Contextual
    var defaultPrice: BigDecimal,
    var purchaseUnitName: String,
    @Contextual
    var taxRate: BigDecimal,
    var defaultVisibilityLevel: VisibilityLevel,
    var sku: String,
    var isActive: Boolean = true,
    var category: SupplierProductCategory
){
    fun realImageUrl(): String {
        return getRealUrl(imageUrl)
    }
}

@Serializable
class SupplierProductCategory(
    val id: Long,
    val supplierId: Long,
    var name: String,
    var defaultVisibilityLevel: VisibilityLevel
)

@Serializable
class OrderBookProductInfoDTO(
    val orderBookId: Long,
    val productId: Long,
    val overrideName: String,
    val note: String,
    @Contextual
    val price: BigDecimal,
    val categoryId: Long,
    var id: Long? = null
)

@Serializable
class OrderBookProductEditModel(
    val overrideName: String? = null,
    val note: String? = null,
    val categoryId: Long
)

@Serializable
class ProductTransFormDTO(
    val id: Long,
    var transFormTo: Long? = null,
    var transFormResourceName: String = "",
    @Contextual
    var transFormAmount: BigDecimal = BigDecimal.ONE,
    var transFormUnitDisplay: String = "",
)

@Serializable
class ProductTransFormEditModel(
    @Contextual
    var transFormAmount: BigDecimal = BigDecimal.ONE,
)

