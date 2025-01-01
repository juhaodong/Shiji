@file:OptIn(ExperimentalMaterial3Api::class)

package domain.composable.dialog.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.selection.SelectOption

class OptionFormField<T : Any>(
    keyName: String,
    label: String = keyName,
    placeHolder: String = "请选择$label",
    required: Boolean = true,
    defaultValue: T? = null,
    validator: (T?) -> Boolean = { true },
    private val options: List<SelectOption<T>>
) : FormField<T>(keyName, label, placeHolder, required, defaultValue, validator) {

    override fun notEmpty(): Boolean {
        return fieldValue != null
    }

    @Composable
    override fun renderFormField(dialogViewModel: DialogViewModel, isLastOne: Boolean) {
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable).fillMaxWidth(),
                value = displayValue(),
                onValueChange = {},
                isError = error.isNotBlank(),
                readOnly = true,
                label = {
                    FormLabelDisplay(label, required)
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                HorizontalDivider()
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.label) },
                        onClick = {
                            fieldValue = selectionOption.value
                            expanded = false
                        }
                    )
                }


            }
        }
    }

    override fun displayValue(): String {
        return options.find { it.value == fieldValue }?.label ?: ""
    }

    override fun reset() {
        fieldValue = if (required && defaultValue == null) {
            options[0].value
        } else {
            defaultValue
        }
    }


}

class AsyncOptionFormField<T : Any>(
    keyName: String,
    label: String = keyName,
    val addNewOption: (suspend () -> SelectOption<T>)? = null,
    private val loadOptions: suspend () -> List<SelectOption<T>>,
    val asyncScope: (suspend () -> Unit) -> Unit,
    placeHolder: String = "请选择$label",
    required: Boolean = true,
    defaultValue: T? = null,
    validator: (T?) -> Boolean = { true },
) : FormField<T>(keyName, label, placeHolder, required, defaultValue, validator) {

    val localOptions = mutableStateListOf<SelectOption<T>>()

    init {
        asyncScope {
            localOptions.addAll(loadOptions())
        }
    }

    override fun notEmpty(): Boolean {
        return fieldValue != null
    }

    override fun reset() {
        fieldValue = if (required && defaultValue == null) {
            null
        } else {
            defaultValue
        }
    }

    override fun displayValue(): String {
        return localOptions.find { it.value == fieldValue }?.label ?: ""
    }

    @Composable
    override fun renderFormField(dialogViewModel: DialogViewModel, isLastOne: Boolean) {
        var expanded by remember { mutableStateOf(false) }
        LaunchedEffect(true) {
            val options = loadOptions()
            if (options.isNotEmpty()) {
                localOptions.clear()
                localOptions.addAll(options)
            }
        }
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable).fillMaxWidth(),
                value = displayValue(),
                onValueChange = {},
                isError = error.isNotBlank(),
                readOnly = true,
                label = {
                    FormLabelDisplay(label, required)
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (addNewOption != null) {
                    DropdownMenuItem(text = {
                        Text("添加新选项")
                    }, onClick = {
                        dialogViewModel.runInScope {
                            addNewOption?.let { it() }.let {
                                if (it != null) {
                                    localOptions.add(it)
                                }
                            }
                        }
                    })
                }
                HorizontalDivider()
                localOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.label) },
                        onClick = {
                            fieldValue = selectionOption.value
                            expanded = false
                        }
                    )
                }


            }
        }
    }

}