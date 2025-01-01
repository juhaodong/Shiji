package dataLayer.model.dish


import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import dataLayer.model.topping.ModOptionModel
import dataLayer.repository.DiscountRepository
import kotlinx.serialization.Serializable
import modules.utils.BigDecimalSerializer
import modules.utils.FormatUtils.sumOfB
import modules.utils.FormatUtils.toPriceDisplay

@Serializable
data class CartModel(
    val dishesModel: DishesModel,
    val selectedModOption: MutableList<Apply> = mutableListOf(),
    var note: String = "",
    var count: Int = 1,
    var tempDiscountStr: String = "",
    @Serializable(with = BigDecimalSerializer::class)
    var overridePrice: BigDecimal? = null,
    var overrideName: String = "",
    var overrideConsumeTypeId: Int? = null,
    val round: Int = 1
) {
    var sourceMark: String? = null
    var active = false
    var order = 0

    constructor(cartModel: CartModel) : this(
        dishesModel = cartModel.dishesModel,
        selectedModOption = cartModel.selectedModOption.toMutableList(),
        note = cartModel.note,
        count = cartModel.count,
        tempDiscountStr = cartModel.tempDiscountStr,
        overridePrice = cartModel.overridePrice,
        overrideConsumeTypeId = cartModel.overrideConsumeTypeId,
        overrideName = cartModel.overrideName
    ) {
        sourceMark = cartModel.sourceMark
        active = cartModel.active
    }

    fun copy(): CartModel {
        return CartModel(this)
    }

    fun getModDisplayString(): String {
        return getDisplayModList(selectedModOption)
            .joinToString(", ") { it.toString() }
    }

    private fun calculateModOptionHashCode(): Int {
        var result = ""
        this.selectedModOption.forEach {
            result += it.hashCode()
        }
        return result.hashCode()
    }

    fun featureValue(): Int {
        var result = 0
        result = 31 * result + dishesModel.code.hashCode()
        result = 31 * result + calculateModOptionHashCode()
        result = 31 * result + tempDiscountStr.hashCode()
        result = 31 * result + note.hashCode()
        result = 31 * result + active.hashCode()
        result = 31 * result + overridePrice.hashCode()
        result = 31 * result + overrideConsumeTypeId.hashCode()
        result = 31 * result + sourceMark.hashCode()
        result = 31 * result + overrideName.hashCode()
        return result
    }

    private fun getAddPrice(): BigDecimal {
        return selectedModOption.sumOfB { it -> it.selectedOptionModel.sumOfB { it.priceInfo } }
    }

    fun getTotalPrice(): BigDecimal {
        return ((overridePrice ?: dishesModel.price)
                ) + getAddPrice()
    }

    fun getRealPrice(): BigDecimal {
        return DiscountRepository.applyDiscountMod(getTotalPrice(), tempDiscountStr)
    }

    override fun hashCode(): Int {
        var result = featureValue()
        result = 31 * result + count
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (null == other) return false
        if (this === other) return true

        other as CartModel

        if (dishesModel != other.dishesModel) return false
        if (selectedModOption != other.selectedModOption) return false
        if (note != other.note) return false
        if (count != other.count) return false
        if (tempDiscountStr != other.tempDiscountStr) return false
        if (active != other.active) return false
        if (sourceMark != other.sourceMark) return false
        if (overrideConsumeTypeId != other.overrideConsumeTypeId) return false
        if (overrideName != other.overrideName) return false
        return overridePrice == other.overridePrice
    }

    fun toOrderItemDTO(): OrderItemDTO {
        return OrderItemDTO(
            dishesId = dishesModel.dishId,
            code = dishesModel.code,
            price = overridePrice ?: getTotalPrice(),
            tempDiscountStr = tempDiscountStr,
            note = note,
            count = count,
            apply = selectedModOption.map {
                it.toApplyDTO()
            },
            isFree = dishesModel.isFree(),
            sourceMark = sourceMark,
            overrideRound = null,
            overrideConsumeTypeId = overrideConsumeTypeId,
            agId = selectedModOption.joinToString(",") { it.groupId.toString() }.ifBlank { null },
            aId = selectedModOption.map { it.selectedOptionModel }.flatten()
                .joinToString(",") { it.id.toString() }.ifBlank { null },
            currentName = overrideName
        )
    }

}

fun getDisplayModList(selectedModOption: List<Apply>): List<DisplayModOption> {
    return selectedModOption
        .map { it.getDisplayModOptionList() }.flatten().filter { it.modOptionModel.id > 0 }
        .groupBy { it.modOptionModel.id }.values.map {
            it.reduce { acc, displayModOption ->
                acc.count += displayModOption.count
                acc
            }
        }.sortedBy { !it.modOptionModel.multiple }

}

@Serializable
data class OrderItemDTO(
    val dishesId: Int,
    val code: String,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val tempDiscountStr: String,
    val note: String,
    var count: Int,
    val apply: List<ApplyDTO>,
    val aId: String?,
    val agId: String?,
    val isFree: Boolean,
    var sourceMark: String? = null,
    val overrideRound: Int? = 1,
    val overrideConsumeTypeId: Int? = null,
    val currentName: String = ""
)

@Serializable
data class Apply(val groupId: Int, val selectedOptionModel: List<ModOptionModel>) {

    fun toApplyDTO(): ApplyDTO {
        return ApplyDTO(groupId, selectedOptionModel.filter { it.id > 0 }.map { it.id })
    }

    fun getDisplayModOptionList(): List<DisplayModOption> {
        return selectedOptionModel.fold(hashMapOf<Int, DisplayModOption>()) { map, mod ->
            val c = map[mod.id]
            if (c == null) {
                map[mod.id] = DisplayModOption(mod)
            } else {
                c.count++
            }
            map
        }.values.toList()
    }

    override fun hashCode(): Int {
        var result = 0
        result = 31 * result + groupId.hashCode()
        result =
            31 * result + selectedOptionModel.map { it.id }.sorted().joinToString(",").hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as Apply

        if (groupId != other.groupId) return false
        return selectedOptionModel == other.selectedOptionModel
    }

}

data class DisplayModOption(val modOptionModel: ModOptionModel) {
    var count = 1
    override fun toString(): String {
        return """${if (count > 1) "$count× " else ""}${modOptionModel.name}${
            if (modOptionModel.priceInfo != BigDecimal.ZERO)
                "(${(modOptionModel.priceInfo * count.toBigDecimal()).toPriceDisplay()})"
            else ""
        }"""

    }
}

@Serializable
data class ApplyDTO(val groupId: Int, val selectId: List<Int>)