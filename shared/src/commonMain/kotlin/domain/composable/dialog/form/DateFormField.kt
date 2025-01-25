@file:OptIn(ExperimentalMaterial3Api::class)

package domain.composable.dialog.form

import LocalDialogManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.raedghazal.kotlinx_datetime_ext.now
import domain.composable.dialog.basic.BeautifulDialog
import domain.composable.dialog.basic.DialogViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDate.Companion
import kotlinx.datetime.toLocalDate
import modules.utils.globalDialogManager

class DateFormField(
    keyName: String,
    label: String = keyName,
    placeHolder: String = "请输入$label",
    required: Boolean = true,
    defaultValue: LocalDate?,
    validator: (LocalDate?) -> Boolean = { true },
    val keyboardType: KeyboardType = KeyboardType.Text
) : FormField<LocalDate>(
    keyName,
    label,
    placeHolder,
    required,
    defaultValue ?: LocalDate.now(),
    validator
) {

    override fun notEmpty(): Boolean {
        return fieldValue != null
    }


    @Composable
    override fun renderFormField(dialogViewModel: DialogViewModel, isLastOne: Boolean) {
        val scope = rememberCoroutineScope()
        val dialogManager = LocalDialogManager.current
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onExpandedChange = {
                scope.launch {
                    try {
                        val date = LocalDate.parse(dialogViewModel.showDatePicker(label))
                        fieldValue = date
                    } catch (e: Exception) {
                        dialogManager.successAnd(e.message ?: "未知错误")
                    }

                }
            }
        ) {
            TextField(
                readOnly = true,
                value = displayValue(),
                label = {
                    FormLabelDisplay(label, required)
                },
                onValueChange = {

                },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable).fillMaxWidth(),
                placeholder = {
                    Text(placeHolder)
                },
                isError = error.isNotBlank(), singleLine = true,
                keyboardOptions = generateKeyboardType(isLastOne, keyboardType)
            )
        }
    }

}




