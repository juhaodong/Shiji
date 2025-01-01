package domain.composable.dialog.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.GrowSpacer
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.selection.SelectOption
import domain.inventory.model.storageItem.UnitDTO
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class DefaultUnitList(
    val name: String, val unitList: List<UnitDTO>
) {
    fun displayName(): String {
        return "$name(" + unitList.joinToString(separator = ",") {
            it.name
        } + ")"
    }
}

val defaultUnitLists = listOf(
    DefaultUnitList(
        name = "重量", unitList = listOf(
            UnitDTO(name = "g", nextLevelFactor = BigDecimal.ONE),
            UnitDTO(name = "kg", nextLevelFactor = BigDecimal.fromInt(1000)),
        )
    ), DefaultUnitList(
        name = "体积", unitList = listOf(
            UnitDTO(name = "ml", nextLevelFactor = BigDecimal.ONE),
            UnitDTO(name = "L", nextLevelFactor = BigDecimal.fromInt(1000)),
        )
    ), DefaultUnitList(
        name = "个数", unitList = listOf(
            UnitDTO(name = "个", nextLevelFactor = BigDecimal.ONE),
            UnitDTO(name = "打", nextLevelFactor = BigDecimal.fromInt(12)),
            UnitDTO(
                name = "箱", nextLevelFactor = BigDecimal.fromInt(5)
            ) // You can adjust the factor as needed
        )
    )
)

class UnitEditorFormField(keyName: String, defaultValue: List<UnitDTO>? = null) :
    FormField<List<UnitDTO>>(keyName, "最小单位名称", defaultValue = defaultValue) {
    val addedUnitList = mutableStateListOf<UnitDTO>()
    var minUnitName by mutableStateOf("")
    override fun reset() {
        loadFromList(defaultValue)
    }

    fun loadFromList(list: List<UnitDTO>?) {
        if (list != null && list.isNotEmpty()) {
            minUnitName = list[0].name
            if (list.size > 1) {
                addedUnitList.clear()
                addedUnitList.addAll(list.subList(1, list.size))
            }
        }
    }

    init {
        reset()
    }

    fun getLastLevelUnitName(index: Int): String {
        val lastLevel = index - 1
        return if (lastLevel == -1) {
            minUnitName
        } else {
            addedUnitList[lastLevel].name
        }
    }


    @Composable
    override fun renderFormField(dialogViewModel: DialogViewModel, isLastOne: Boolean) {
        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                minUnitName,
                onValueChange = {
                    minUnitName = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(placeHolder)
                },
                trailingIcon = {
                    Text(if (minUnitName.isBlank()) "选择默认单位组" else "添加更多单位",
                        modifier = Modifier.padding(end = 16.dp).padding(4.dp).clickable {
                            dialogViewModel.runInScope {
                                if (minUnitName.isBlank()) {
                                    val option =
                                        dialogViewModel.showSelectDialog("请选择一个默认单位组",
                                            defaultUnitLists.map {
                                                SelectOption(
                                                    label = it.displayName(), value = it.unitList
                                                )
                                            })
                                    loadFromList(option)
                                } else {
                                    val dto: UnitDTO = dialogViewModel.showFormDialog(

                                        TextFormField(
                                            "name", "单位名称"
                                        ), TextFormField(
                                            "nextLevelFactor",
                                            "和上一级单位的转化比率",
                                            keyboardType = KeyboardType.Decimal
                                        ), title = "单位详情", subtitle = "请填写单位详情"

                                    )
                                    addedUnitList.add(dto)
                                }

                            }
                        })
                },
                isError = error.isNotBlank(),
                label = {
                    FormLabelDisplay(label, required)
                },
                singleLine = true,
                keyboardOptions = generateKeyboardType(isLastOne),
            )
            addedUnitList.forEachIndexed { index, it ->
                BaseSurface {
                    Row(
                        modifier = Modifier.clickable {
                            addedUnitList.removeAt(index)
                        }.padding(16.dp), verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(it.name + " = ")
                        Text(it.nextLevelFactor.toPlainString())
                        Text(getLastLevelUnitName(index))
                        GrowSpacer()

                        Icon(Icons.Default.Delete, contentDescription = null)

                    }
                }

            }


        }
    }

    override var fieldValue: List<UnitDTO>?
        get() {
            if (minUnitName.isNotBlank()) {
                return listOf(UnitDTO(minUnitName, BigDecimal.ONE), *addedUnitList.toTypedArray())
            } else {
                return null
            }
        }
        set(value) {}

    @OptIn(ExperimentalSerializationApi::class)
    override fun valueToElement(): JsonElement {
        return fieldValue?.map {
            JsonObject(
                content = mapOf(
                    "name" to JsonPrimitive(it.name),
                    "nextLevelFactor" to JsonPrimitive(it.nextLevelFactor.toPlainString())
                )
            )

        }?.let { JsonArray(content = it) } ?: JsonPrimitive(null)
    }

    override fun notEmpty(): Boolean {
        return minUnitName.isNotBlank()
    }
}