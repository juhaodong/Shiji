package dataLayer.repository


import androidx.compose.runtime.mutableStateListOf
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import dataLayer.model.dish.CartModel
import dataLayer.model.dish.OrderItemDTO
import me.tatarka.inject.annotations.Inject
import modules.utils.FormatUtils.sumOfB


@Inject
class DishDocker {
    private val cartModelMap: HashMap<Int, CartModel> = HashMap()
    val orderItemList = mutableStateListOf<CartModel>()
    var order = 0


    fun getDTOList(): List<OrderItemDTO> {
        return orderItemList.map { it.toOrderItemDTO() }
    }

    fun reset() {
        order = 0
        orderItemList.clear()
        cartModelMap.clear()
    }

    fun loadList(list: List<CartModel>, reset: Boolean = true) {
        if (reset) {
            reset()
        }
        list.forEach { changeCountForItem(it, it.count) }
    }

    fun changeCountForItem(cartModel: CartModel, count: Int, sortItem: Boolean = true): Boolean {
        val itemToAdd = cartModel.copy()
        val k = itemToAdd.featureValue()

        if (cartModelMap.containsKey(k)) {
            val modifyItem = cartModelMap[k] ?: return false
            if (modifyItem.count + count > 0) {
                if (sortItem) {
                    modifyItem.order = order++
                }
                modifyItem.count += count
            } else {
                modifyItem.count = 0
                cartModelMap.remove(k)
            }
        } else if (count > 0) {
            itemToAdd.count = count
            if (sortItem) {
                itemToAdd.order = order++
            }
            cartModelMap[k] = itemToAdd
        }
        afterOrderItemMapChange()
        return true
    }

    fun activeList(): List<CartModel> {
        return orderItemList.filter { it.active }
    }

    fun activeCount(): Int {
        return activeList().sumOf { it.count }
    }

    fun activeTotalPrice(): BigDecimal {
        return activeList().sumOfB { it.getRealPrice() * it.count.toBigDecimal() }
    }

    fun count(): Int {
        return orderItemList.sumOf { it.count }
    }

    fun totalPrice(): BigDecimal {
        return orderItemList.sumOfB { it.getRealPrice() * it.count.toBigDecimal() }
    }

    private fun afterOrderItemMapChange() {
        orderItemList.clear()
        orderItemList.addAll(cartModelMap.values)

    }
}
