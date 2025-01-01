@file:OptIn(ExperimentalMaterial3Api::class)

package domain.composable.dialog.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import domain.composable.basic.layout.SmallSpacer
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.selection.SelectOption
import domain.inventory.model.change.AmountAtUnit
import domain.inventory.model.storageItem.ItemUnit
import domain.inventory.model.storageItem.getLowestUnitCount
import domain.inventory.model.storageItem.getUnitDisplay
import io.github.aakira.napier.Napier
import kotlin.uuid.ExperimentalUuidApi


class StorageAmountFormField(keyName: String, val unitList: List<ItemUnit>) :
    FormField<String>(keyName, "操作数量") {

    var selectedUnit by mutableStateOf(unitList.last())
    var textInput by mutableStateOf("")
    var multipleAddMode by mutableStateOf(false)

    override fun notEmpty(): Boolean {
        return fieldValue?.isNotBlank() == true
    }

    fun displayText(): String {
        if (multipleAddMode) {
            return getUnitDisplay(fieldValue?.toBigDecimal() ?: BigDecimal.ZERO, unitList)
        } else {
            return textInput
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun renderFormField(dialogViewModel: DialogViewModel, isLastOne: Boolean) {
        var expanded by remember { mutableStateOf(false) }




        ExposedDropdownMenuBox(modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }) {
            LaunchedEffect(textInput to selectedUnit) {
                if (textInput.isNotBlank()) {
                    fieldValue = getLowestUnitCount(
                        textInput.toBigDecimal(), selectedUnit.id, unitList
                    ).toPlainString()

                }
            }
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = displayText(),
                readOnly = multipleAddMode,
                onValueChange = { textInput = it },
                isError = error.isNotBlank(),
                placeholder = {
                    Text("请输入想要操作的数量，您可以在随后继续添加数量")
                },
                label = {
                    FormLabelDisplay(label, required)
                },
                trailingIcon = {
                    Row(
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        if (!multipleAddMode) {
                            Row(modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable)) {
                                Text(selectedUnit.name)
                                SmallSpacer(4)
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded,
                                )
                            }
                        }
                        if (textInput.isNotBlank()) {
                            SmallSpacer(4)
                            Icon(Icons.Default.AddCircle,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    dialogViewModel.runInScope {
                                        val newAmount: AmountAtUnit =
                                            dialogViewModel.showFormDialog(

                                                TextFormField(
                                                    keyName = "amount",
                                                    label = "数量",
                                                    keyboardType = KeyboardType.Number
                                                ),
                                                OptionFormField(
                                                    keyName = "unitId", options = unitList.map {
                                                        SelectOption(it.name, it.id)
                                                    }, defaultValue = unitList.last().id
                                                ),
                                            )
                                        Napier.e {
                                            fieldValue.toString() + "@text:" + newAmount.amount + "unit:" + newAmount.unitId
                                        }
                                        fieldValue =
                                            (fieldValue!!.toBigDecimal() + getLowestUnitCount(
                                                newAmount.amount, newAmount.unitId, unitList
                                            )).toPlainString()
                                        Napier.e {
                                            getUnitDisplay(
                                                fieldValue?.toBigDecimal() ?: BigDecimal.ZERO,
                                                unitList
                                            )
                                        }
                                        multipleAddMode = true
                                    }
                                })
                        }

                    }

                },
                keyboardOptions = generateKeyboardType(isLastOne, KeyboardType.Number)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                HorizontalDivider()
                unitList.forEach { selectionOption ->
                    DropdownMenuItem(text = { Text(textInput + selectionOption.name) }, onClick = {
                        selectedUnit = selectionOption
                        expanded = false
                    })
                }
            }
        }
    }
}