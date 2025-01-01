@file:OptIn(ExperimentalUuidApi::class)

package domain.inventory.model.storageItem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

const val proxyUrl = "https://cloud-v2.aaden.io/downloadFile?fileUrl="
fun getRealUrl(originUrl: String) = proxyUrl + originUrl
fun String.imageWithProxy() = proxyUrl + this

@Serializable
class StorageItemModel(
    var id: Long,
    val shopId: Long,
    var name: String,
    var imageUrl: String = "",
    @Contextual var unitPrice: BigDecimal,
    @Contextual var currentCount: BigDecimal,
    var unitDisplay: String,
    var units: List<ItemUnit> = listOf(),
    var primarySku: String,
    var storageItemCategory: ResourceCategoryModel,
    @Contextual var maxLevelFactor: BigDecimal = BigDecimal.ONE,
    var inventoryPeriodDays: Int,
    @Contextual var periodOutAmount: BigDecimal = BigDecimal.ZERO,
    var periodOutUnitDisplay: String = "",
) {

    fun realImageUrl(): String {
        return getRealUrl(imageUrl)
    }


    fun unitDisplay(count: BigDecimal? = null): String {
        return getUnitDisplay(count ?: currentCount, units)
    }

    fun maxUnitPrice(): BigDecimal {
        return (unitPrice * maxLevelFactor).roundToDigitPositionAfterDecimalPoint(
            2, RoundingMode.ROUND_HALF_TOWARDS_ZERO
        )
    }

    fun consumeRatio(): Float {
        return (currentCount.floatValue(false) / (periodOutAmount.floatValue(false)
            .coerceAtLeast(1f) * 2f)
                )
            .coerceIn(0f, 1f)
    }

    fun safeDays(): BigDecimal {
        try {
            return ((currentCount.divide(
                periodOutAmount,
                decimalMode = DecimalMode.US_CURRENCY
            )) * inventoryPeriodDays).roundToDigitPositionAfterDecimalPoint(
                0,
                RoundingMode.ROUND_HALF_TOWARDS_ZERO
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return BigDecimal.ZERO
        }
    }

    @Composable
    fun getShortageColor(): Color {
        val consumeRatio = consumeRatio()
        return when {
            consumeRatio <= 0.25f -> Color(0xFFF5B7B1) // Light red (most severe shortage)
            consumeRatio <= 0.5f -> Color(0xFFFAD7A0) // Light orange
            consumeRatio <= 0.75f -> Color(0xFFFFFDD0) // Light yellow
            else -> MaterialTheme.colorScheme.primaryContainer.copy(0.6f)
        }
    }

    fun getShortageLabel(): String {
        val consumeRatio = consumeRatio()
        return when {
            consumeRatio <= 0.25f -> "严重不足" // Severe shortage
            consumeRatio <= 0.5f -> "库存紧张" // Moderate shortage
            consumeRatio <= 0.75f -> "库存充足" // Sufficient
            else -> "库存充裕" // Abundant
        }
    }
}

fun getLowestUnitCount(count: BigDecimal, uuid: Uuid, unitList: List<ItemUnit>): BigDecimal {
    return count * unitList.find { it.id == uuid }?.minLevelFactor!!
}

fun getUnitDisplay(amountOnMinUnit: BigDecimal, unitList: List<ItemUnit>): String {
    if (unitList.isEmpty()) {
        return "-"
    }
    if (amountOnMinUnit eq BigDecimal.ZERO) {
        return "0" + unitList[0].name
    } else {
        val result = mutableListOf<String>()
        var maxFactor = unitList.last().minLevelFactor.floatValue(false).toInt()
        var rest = amountOnMinUnit.floatValue(false).toInt()
        unitList.reversed().forEach {
            val value = rest.floorDiv(maxFactor)
            val reminder = rest.mod(maxFactor)
            if (!(value != 0)) {
                result.add(
                    value.toString() + it.name
                )
            }
            maxFactor /= it.nextLevelFactor.floatValue(false).toInt()
            rest = reminder
        }
        return result.joinToString("")
    }
}

@Serializable
data class ItemUnit(
    val name: String = "",
    @Contextual val nextLevelFactor: BigDecimal,
    @Contextual var minLevelFactor: BigDecimal,
    @Contextual val id: Uuid
)

infix fun BigDecimal.eq(other: BigDecimal): Boolean {
    return this.compareTo(other) == 0
}

infix fun BigDecimal.notEq(other: BigDecimal): Boolean {
    return this.compareTo(other) != 0
}


@Composable
fun Float.getRatioColor(): Color {
    val consumeRatio = this
    return when {
        consumeRatio <= 0.25f -> Color(0xFFF5B7B1) // Light red (most severe shortage)
        consumeRatio <= 0.5f -> Color(0xFFFAD7A0) // Light orange
        consumeRatio <= 0.75f -> Color(0xFFFFFDD0) // Light yellow
        else -> MaterialTheme.colorScheme.primaryContainer.copy(0.6f)
    }
}