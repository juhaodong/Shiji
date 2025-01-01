package domain.supplier.model

import com.raedghazal.kotlinx_datetime_ext.now
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class OrderBookCategory(
    val id:Long?,
    val orderBookId: Long,
    val name: String,
    val displayName: String,
    val createdBy: String,
    val lastUpdate: LocalDateTime = LocalDateTime.now(),
) {
    fun getRealName(): String {
        return displayName.ifBlank {
            name
        }
    }
}