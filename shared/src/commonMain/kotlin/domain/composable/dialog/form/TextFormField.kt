package domain.composable.dialog.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import domain.composable.dialog.basic.DialogViewModel

class TextFormField(
    keyName: String,
    label: String = keyName,
    placeHolder: String = "请输入$label",
    required: Boolean = true,
    defaultValue: String? = "",
    validator: (String?) -> Boolean = { true },
    val keyboardType: KeyboardType = KeyboardType.Text
) : FormField<String>(keyName, label, placeHolder, required, defaultValue ?: "", validator) {

    override fun notEmpty(): Boolean {
        return fieldValue?.isNotEmpty() ?: false
    }

    @Composable
    override fun renderFormField(dialogViewModel: DialogViewModel, isLastOne: Boolean) {
        TextField(
            value = displayValue(),
            label = {
                FormLabelDisplay(label, required)
            },
            onValueChange = {
                fieldValue = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(placeHolder)
            },
            isError = error.isNotBlank(), singleLine = true,
            keyboardOptions = generateKeyboardType(isLastOne, keyboardType)
        )
    }

    companion object {
        fun getNoteField(
            required: Boolean = false,
            keyName: String = "note",
            defaultValue: String? = null
        ): TextFormField {
            return TextFormField(
                keyName,
                "备注",
                required = required,
                defaultValue = defaultValue
            )
        }
    }
}




