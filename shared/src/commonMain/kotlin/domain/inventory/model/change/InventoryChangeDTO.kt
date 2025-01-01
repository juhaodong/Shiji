@file:OptIn(ExperimentalUuidApi::class)

package domain.inventory.model.change

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class InventoryChangeDTO(
    val resourceId: Long,
    @Contextual
    val countOnUnit: BigDecimal,
    @Contextual
    val overridePrice: BigDecimal? = null,
    val note: String = "",
    val isLoss: Boolean? = false
)

@Serializable
class InventoryEnterModel(
    @Contextual
    val amount: BigDecimal,
    @Contextual
    val overridePrice: BigDecimal? = null,
    val note: String = "",
)


@Serializable
class InventoryExitModel(
    @Contextual
    val amount: BigDecimal,
    val note: String = "",
)

@Serializable
class InventoryCorrectModel(
    @Contextual
    val amount: BigDecimal,
    val note: String = "",
    val isLoss: Boolean? = false
)

fun InventoryCorrectModel.toDTO(resourceId: Long): InventoryChangeDTO {
    return InventoryChangeDTO(
        resourceId = resourceId,
        countOnUnit = amount,
        overridePrice = null, // Correct doesn't have override price
        note = note,
        isLoss = isLoss ?: false
    )
}

fun InventoryEnterModel.toDTO(resourceId: Long, maxLevelFactor: BigDecimal): InventoryChangeDTO {
    return InventoryChangeDTO(
        resourceId = resourceId,
        countOnUnit = amount,
        overridePrice = overridePrice?.scale(30)?.divide(maxLevelFactor, DecimalMode.US_CURRENCY),
        note = note,
        isLoss = false // Enter is not a loss
    )
}

fun InventoryExitModel.toDTO(resourceId: Long, isLoss: Boolean): InventoryChangeDTO {
    return InventoryChangeDTO(
        resourceId = resourceId,
        countOnUnit = amount,
        overridePrice = null, // Exit doesn't have override price
        note = note,
        isLoss = isLoss
    )
}

@Serializable
class AmountAtUnit(@Contextual val unitId: Uuid, @Contextual val amount: BigDecimal)