package domain.composable.dialog.basic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raedghazal.kotlinx_datetime_ext.LocalDateTimeFormatter
import com.raedghazal.kotlinx_datetime_ext.Locale
import domain.composable.dialog.form.AsyncOptionFormField
import domain.composable.dialog.form.FormField
import domain.composable.dialog.form.FormSchema
import domain.composable.dialog.inputDialog.InputDialogRepository
import domain.composable.dialog.selection.SelectOption
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import modules.network.AppScope
import kotlin.coroutines.resume

@AppScope
@Inject
class DialogViewModel(val inputDialogRepository: InputDialogRepository, val json: Json) :
    ViewModel() {


    var showDialog by mutableStateOf(false)
    var title by mutableStateOf("")
    var selectOptions = mutableStateListOf<SelectOption<*>>()
    var confirmCallBack: ((value: Any) -> Unit)? by mutableStateOf(null)
    var twoRow by mutableStateOf(false)
    var searchText by mutableStateOf("")
    suspend fun <T> showSelectDialog(
        title: String,
        options: List<SelectOption<T>>,
        twoRow: Boolean = false,
    ): T {
        showDialog = true
        this.title = title
        this.twoRow = twoRow
        searchText = ""
        this.selectOptions.clear()
        this.selectOptions.addAll(options)

        val res = suspendCancellableCoroutine { re ->
            confirmCallBack = {
                showDialog = false
                re.resumeWith(Result.success(it))
            }
        }
        @Suppress("UNCHECKED_CAST") return res as T
    }

    suspend fun showInput(
        title: String, defaultValue: String = "", type: KeyboardType = KeyboardType.Text
    ): String {
        return inputDialogRepository.showInput(
            title = title, defaultValue = defaultValue, type = type
        )
    }

    var datePickerDialogShow by mutableStateOf(false)
    var datePickerTitle by mutableStateOf("")
    private val dateFormatter =
        LocalDateTimeFormatter.ofPattern("yyyy-MM-dd", locale = Locale.default())

    private var datePickerConfirmCallBack: ((value: String) -> Unit)? by mutableStateOf(null)

    suspend fun showDatePicker(title: String): String {
        datePickerDialogShow = true
        datePickerTitle = title
        return suspendCancellableCoroutine { continuation ->
            datePickerConfirmCallBack = {
                datePickerDialogShow = false
                continuation.resume(it)
            }
        }
    }

    fun submit(selectedDate: Long) {
        datePickerConfirmCallBack?.invoke(
            dateFormatter.format(
                Instant.fromEpochMilliseconds(selectedDate)
                    .toLocalDateTime(timeZone = TimeZone.currentSystemDefault()).date
            )
        )
    }


    val formSchemaList = mutableStateListOf<FormSchema>()


    suspend inline fun <reified T> showFormDialog(
        vararg formFields: FormField<*>,
        title: String = "",
        subtitle: String? = null,
    ): T {
        val schema = FormSchema(formFields.toList(), json).title(title, subtitle)
        formSchemaList.add(schema)
        schema.show = true
        schema.formDispose = {
            schema.show = false
            formSchemaList.remove(schema)
        }

        return suspendCancellableCoroutine { continuation ->
            schema.formDialogConfirmCallBack = {
                schema.formDispose()
                continuation.resume(json.decodeFromString(it))

            }
        }
    }

    fun runInScope(action: suspend () -> Unit) {
        viewModelScope.launch {
            action()
        }
    }


    fun <R : Any> createAsyncOptionFormField(
        keyName: String,
        label: String,
        addNewOption: (suspend () -> SelectOption<R>),
        loadOptions: suspend () -> List<SelectOption<R>>,
        defaultValue: R? = null,
        required: Boolean = true,
        validator: (R?) -> Boolean = { true }
    ): AsyncOptionFormField<R> {
        return AsyncOptionFormField<R>(
            keyName = keyName,
            label = label,
            addNewOption = addNewOption,
            loadOptions = loadOptions,
            asyncScope = this::runInScope,
            required = required,
            defaultValue = defaultValue,
            validator = validator
        )
    }
}