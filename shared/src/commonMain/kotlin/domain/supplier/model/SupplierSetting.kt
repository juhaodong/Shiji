package domain.supplier.model

import com.raedghazal.kotlinx_datetime_ext.now
import domain.inventory.model.BankInfo
import domain.inventory.model.ContactInfo
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class SupplierSetting(
    var name: String,
    val description: String,
    val imageUrl: String?,
    var telephone: String,
    var email: String,
    var contactInfo: ContactInfo,
    var bankInfo: BankInfo,
    var keywords: String = "",
    var publicVisible: Boolean = true,
    var orderBookAutoReady: Boolean = false,
    var activeCode: String,
    val id: Long
)

@Serializable
class SupplierOrderBookDTO(
    val supplier: SupplierSetting,
    val orderBook: OrderBook,
    val shopInfo: ShopInfo
) {
    fun displayName(): String {
        return orderBook.displayName.ifBlank { supplier.name }
    }
}

@Serializable
class ShopInfo(
    val shopId: Long,
    val contactInfo: ContactInfo,
    val email: String,
    val lastUpdate: LocalDateTime = LocalDateTime.now(),
    var bindingUUID: String,
    val mainUserId: String?
) {
    fun canBind(): Boolean {
        return mainUserId == null
    }
}

@Serializable
class OrderBook(
    val id: Long,
    val shopId: Long,
    val supplierId: Long,
    var displayName: String,
    var customerReference: String,
    val ready: Boolean = false,
    val lastUpdate: LocalDateTime = LocalDateTime.now()
)