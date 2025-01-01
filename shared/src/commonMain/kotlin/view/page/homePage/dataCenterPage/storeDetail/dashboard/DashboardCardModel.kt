package view.page.homePage.dataCenterPage.storeDetail.dashboard

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import modules.utils.BigDecimalSerializer
import modules.utils.FormatUtils.toPriceDisplay
import modules.utils.dateWithWeekDay
import modules.utils.toPercentageDisplay


@Serializable
enum class DashboardCardType {
    TOTAL_REVENUE,        // 营业额
    TOTAL_ORDERS,         // 订单数量
    GUEST_COUNT,          // 用餐人数
    TAKEAWAY_COUNT,       // 外卖数量
    TAKEAWAY_TOTAL,       // 外卖价格
    DINE_IN_COUNT,        // 堂食数量
    AVERAGE_ORDER_VALUE,  // 客单价
    UNPAID_ORDERS_COUNT,  // 未结账订单数量
    UNPAID_ORDERS_AMOUNT, // 未结账订单金额
    TURNOVER_RATE,        // 翻台率
    OPEN_TABLE_RATE,       // 开台率
    TOTAL_DISCOUNT, TOTAL_RETURN
}


fun formatDashboardCardValue(value: BigDecimal, type: DashboardCardType): String {
    return when (type) {
        DashboardCardType.TURNOVER_RATE, DashboardCardType.OPEN_TABLE_RATE -> {
            value.toPercentageDisplay()
        }

        DashboardCardType.TOTAL_ORDERS, DashboardCardType.GUEST_COUNT, DashboardCardType.TAKEAWAY_COUNT, DashboardCardType.DINE_IN_COUNT, DashboardCardType.UNPAID_ORDERS_COUNT ->
            value.toPlainString()

        else -> value.toPriceDisplay()
    }
}


@Serializable
class DashboardChangeDTO(
    val date: LocalDate,
    @Serializable(with = BigDecimalSerializer::class)
    val currentValue: BigDecimal
) {
    fun getLabel(): String {
        return date.dateWithWeekDay()
    }
}

@Serializable
data class DashboardCard(
    val type: DashboardCardType,
    val name: String,
    val description: String,
    @Contextual
    val currentValue: BigDecimal,
    @Contextual
    val previousPeriodValue: BigDecimal?,
    @Contextual
    val previousPeriodChange: BigDecimal?,
    val changesInTime: List<DashboardChangeDTO>,
    @Contextual
    val lastWeekValue: BigDecimal?,
    @Contextual
    val lastWeekChange: BigDecimal?,
)