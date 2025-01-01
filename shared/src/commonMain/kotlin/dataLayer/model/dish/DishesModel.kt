package dataLayer.model.dish


import com.ionspin.kotlin.bignum.decimal.BigDecimal
import dataLayer.model.topping.ModGroupDTO
import dataLayer.model.topping.ModGroupModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import modules.utils.BigDecimalSerializer


fun isImageUrl(url: String): Boolean {
    val imageExtensions = listOf(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp")
    return imageExtensions.any { url.endsWith(it, true) }
}

@Serializable
data class DishesModel(
    val dishId: Int,
    var code: String,
    @SerialName("dishName")
    var name: String,
    @SerialName("dishDesc")
    val desc: String = "",
    val cookingTime: Int? = 0,
    @Serializable(with = BigDecimalSerializer::class)
    var price: BigDecimal,
    val categoryId: Int,
    var dishesCategoryTypeId: Int,

    @SerialName("modInfo")
    val modInfoDTOList: List<ModGroupDTO>? = listOf(),
    var color: String? = null,
    var overrideColor: String? = null,
    val image: String? = null,
    var id: Int? = 0,
    var isActive: Int = 0,
    var isFree: Int = 0,
    val isFavorite: Int = 0,

    @SerialName("AllergenName")
    val allergen: String? = "",

    @SerialName("AllergenId")
    val allergenId: String? = ""
) {
    var modInfoList = listOf<ModGroupModel>()
        get() {
            if (field != null && field.isNotEmpty()) {
                return field
            } else {
                field = try {
                    modInfoDTOList?.map {
                        it.toModel()
                    }?.filter { it.optionModelList.size > 0 } ?: listOf()
                } catch (e: Exception) {
                    e.printStackTrace()
                    listOf()
                }

            }
            return field
        }


    var count = 0

    fun allergenIdList(): List<Int> {
        return allergenId?.split(",")?.mapNotNull { it.toIntOrNull() } ?: listOf()
    }

    fun isFree(): Boolean {
        return isFree == 1
    }

    fun hasMod(): Boolean {
        return modInfoList.isNotEmpty()
    }

    fun allergen(): String {
        return allergen ?: ""
    }


    fun haveImage(): Boolean {
        return image?.let { isImageUrl(it) } == true
    }


    fun displayName(): String {
        return code.replace(".", "") + "." + name
    }


}


