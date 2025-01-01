package dataLayer.model.topping

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlinx.serialization.Serializable
import modules.utils.BigDecimalSerializer

@Serializable
data class ModGroupModel(
    val id: Int,
    val name: String,
    val optionModelList: MutableList<ModOptionModel>,
    val required: Boolean,
    val multiple: Boolean,
    val isActive: Boolean = true,
    val maxCount: Int
)

@Serializable
data class ModOptionModel(
    val name: String,
    val id: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val priceInfo: BigDecimal,
    val groupId: Int,
    val groupName: String,
    val required: Boolean = true,
    val multiple: Boolean = false,
    val image: String,
    val maxCount: Int,
)

@Serializable
data class ModGroupDTO(
    val selectName: String?,
    val selectValue: String?,
    val priceInfo: String?,
    val required: Int,
    val multiSelect: Int,
    val selectImage: String?,
    val id: Int,
    val name: String?,
    val maxCount: Int? = -1,
    val isActive: Int? = 1
) {
    fun toModel(): ModGroupModel {
        val modGroupModel = ModGroupModel(
            id = id,
            name = name ?: "",
            optionModelList = mutableListOf(),
            required = required == 1,
            multiple = multiSelect == 1,
            maxCount = maxCount ?: -1,
            isActive = isActive == null || isActive == 1
        )
        if (selectName != null && priceInfo != null && selectValue != null && selectImage != null) {
            if (selectName.contains(',') && selectValue.contains(',') && priceInfo.contains(',')) {
                val selectNameList = selectName.split(",")
                val selectValueList = selectValue.split(",")
                val selectImageList = selectImage.split(',')
                val priceInfoList = priceInfo.split(",")
                for ((index, value) in selectNameList.withIndex()) {
                    modGroupModel.optionModelList.add(
                        ModOptionModel(
                            name = value,
                            id = selectValueList[index].toInt(),
                            priceInfo = priceInfoList[index].toBigDecimal(),
                            groupId = id,
                            groupName = modGroupModel.name,
                            image = selectImageList[index],
                            required = modGroupModel.required,
                            multiple = modGroupModel.multiple,
                            maxCount = maxCount ?: -1
                        )
                    )
                }
            } else {
                if (selectName.isNotBlank() && selectValue.isNotBlank() && priceInfo.isNotBlank()) {
                    modGroupModel.optionModelList.add(
                        ModOptionModel(
                            name = selectName,
                            id = selectValue.toInt(),
                            priceInfo = priceInfo.toBigDecimal(),
                            groupId = id,
                            groupName = modGroupModel.name,
                            image = selectImage,
                            required = modGroupModel.required,
                            multiple = modGroupModel.multiple,
                            maxCount = maxCount ?: -1
                        )
                    )
                }
            }
        }
        return modGroupModel
    }
}



