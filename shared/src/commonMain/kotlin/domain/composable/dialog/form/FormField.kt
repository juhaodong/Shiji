package domain.composable.dialog.form


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import domain.composable.dialog.basic.DialogViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive


abstract class FormField<T>(
    val keyName: String,
    val label: String = keyName,
    val placeHolder: String = "请输入$label",
    val required: Boolean = true,
    val defaultValue: T? = null,
    val validator: (T?) -> Boolean = { true },
) {
    open var fieldValue by mutableStateOf(defaultValue)
    var error by mutableStateOf("")
    var loading by mutableStateOf(false)
    open fun reset() {
        fieldValue = defaultValue
    }



    fun validate(): Boolean {
        return if (required) notEmpty() && validator(fieldValue) else validator(fieldValue)
    }

    open fun displayValue(): String {
        return (fieldValue ?: "").toString()
    }

    open fun valueToElement(): JsonElement {
        return JsonPrimitive(fieldValue.toString())
    }

    abstract fun notEmpty(): Boolean

    @Composable
    open fun render(dialogViewModel: DialogViewModel, index: Int, isLastOne: Boolean) {
        BasicFormField {
            renderFormField(dialogViewModel, isLastOne)
        }
    }

    @Composable
    abstract fun renderFormField(dialogViewModel: DialogViewModel, isLastOne: Boolean)


}


class FormSchema(
//    vararg formFields: FormField<*>,
    val fields: List<FormField<*>>,
    val json: Json,
) {

    var show by mutableStateOf(false)
    var formDialogConfirmCallBack: ((value: String) -> Unit)? by mutableStateOf(null)
    lateinit var formDispose: () -> Unit
    var title: String = "表单"
    var subtitle: String = "填写表单"
    fun validateAll(): Boolean {
        return fields.all { it.validate() }
    }

    fun title(title: String, subtitle: String? = null): FormSchema {
        this.title = title
        if (subtitle != null) {
            this.subtitle = subtitle
        }
        return this
    }

    fun submitForm() {
        val currentFormSchema = this
        if (currentFormSchema.validateAll()) {
            // Access field values using field.fieldValue
            val fieldValues =
                currentFormSchema.fields.associate { it.keyName to it.valueToElement() }
            val string = json.encodeToString(fieldValues)
            formDialogConfirmCallBack?.invoke(string)
        } else {
            // Handle validation errors if any
            currentFormSchema.fields.forEach { field ->
                if (!field.validate()) {
                    // Update field.error with the error message
                    field.error = "Validation failed for ${field.label}"
                }
            }
        }
    }
}


@Composable
fun BasicFormField(
    renderFormField: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        renderFormField()
    }
}

fun generateKeyboardType(
    isLastOne: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,

    ): KeyboardOptions {
    return KeyboardOptions(
        keyboardType = keyboardType,
        imeAction = if (isLastOne) ImeAction.Done else ImeAction.Next
    )
}

@Composable
fun FormLabelDisplay(label: String, required: Boolean) {
    Text(label + if (required) "*" else "")
}

