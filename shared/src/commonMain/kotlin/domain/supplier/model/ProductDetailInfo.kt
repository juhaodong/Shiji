package domain.supplier.model

import kotlinx.serialization.Serializable


@Serializable
class ProductDetailInfo(
    val product: SupplierProduct,
    val itemInfo: OrderBookItemInfo? = null,
    val imported: Boolean
)
