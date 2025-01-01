package dataLayer.model.dish

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import dataLayer.model.topping.ModOptionModel
import kotlinx.serialization.Serializable
import modules.utils.BigDecimalSerializer


@Serializable
data class OrderDishDTO(
    val code: String,
    val name: String,
    val currentName: String = "",
    val note: String = "",
    val sumCount: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val categoryId: Int,
    val categoryTypeId: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val originPrice: BigDecimal = BigDecimal.ZERO,
    val tempDiscountStr: String = "",
    val overrideConsumeTypeId: Int? = null,
    val aName: String? = null,
    val agName: String? = null,
    val aId: String? = null,
    val agId: String? = null,
    val priceInfo: String? = null,
    val round: String? = null,
    val sourceMark: String? = null
) {
    fun toCartModel(
        originDishFinder: (code: String) -> DishesModel?
    ): CartModel? {
        val originDish = (originDishFinder(code) ?: return null)
        val list: MutableList<Apply> = mutableListOf()
        if (!aName.isNullOrBlank()) {
            val infoList = listOf(
                agName,
                aName,
                priceInfo,
                aId,
                agId
            )
            val (agList, aList, priceList, aIdList, agIdList) = infoList.map { it?.split(',') }
            if (agList != null && aList != null && priceList != null && aIdList != null && agIdList != null) {
                for ((index, aName) in aList.withIndex()) {
                    val selectedList = try {
                        listOf(
                            ModOptionModel(
                                name = aName,
                                id = aIdList[index].toInt(),
                                priceInfo = priceList[index].toBigDecimal(),
                                groupId = agIdList[index].toInt(),
                                groupName = agList.getOrElse(index) { "" },
                                maxCount = -1,
                                image = ""
                            )
                        )
                    } catch (e: Exception) {
                        listOf()
                    }
                    list.add(
                        Apply(agIdList[index].toInt(), selectedList)
                    )
                }
            }
        }
        return CartModel(
            dishesModel = originDish,
            selectedModOption = list,
            note = note,
            count = sumCount,
            tempDiscountStr = tempDiscountStr,
            overrideConsumeTypeId = overrideConsumeTypeId,
            round = round?.toIntOrNull() ?: 1
        ).apply {
            this.sourceMark = this@OrderDishDTO.sourceMark
            if (code.startsWith("ea")) {
                overridePrice = price
                overrideName = currentName.ifBlank { dishesModel.name }
            }
        }
    }
}
