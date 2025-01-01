package dataLayer.model.order

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.Serializable
import modules.utils.BigDecimalSerializer


@Serializable
data class BuffetDishesModel(
    val dishesId: Int,
    val name: String,
    val code: String,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val consumeTypeId: Int
)
