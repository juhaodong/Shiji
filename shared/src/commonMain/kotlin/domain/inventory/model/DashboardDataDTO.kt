package domain.inventory.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


@Serializable
class HourSalesDTO(val hour: String, @Contextual val value: BigDecimal)


@Serializable
class DashboardDataDTO(
    @Contextual val currentValue: BigDecimal,
    @Contextual val tillNowChanges: BigDecimal,
    @Contextual val notEnoughResourceCount: BigDecimal,
    @Contextual val totalSales: BigDecimal,
    @Contextual val totalOut: BigDecimal,
    @Contextual val totalLoss: BigDecimal,
    @Contextual val rotationRate: BigDecimal,
    @Contextual val resourceTotalCount: BigDecimal,
    val lastRecordTime: LocalDateTime,
    val last24HourList: List<HourSalesDTO>,
    val inventorySetting: InventorySetting,
)