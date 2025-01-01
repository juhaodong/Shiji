package domain.supplier.model

import kotlinx.serialization.Serializable

@Serializable
class ImportOrderBookProductInfoDTO(val productIds: Map<Long, Boolean>)

@Serializable
class ProductImportedDTO(val product: SupplierProduct, val imported: Boolean)

