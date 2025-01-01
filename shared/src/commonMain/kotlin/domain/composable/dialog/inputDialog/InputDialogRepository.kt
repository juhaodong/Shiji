package domain.composable.dialog.inputDialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.suspendCancellableCoroutine
import me.tatarka.inject.annotations.Inject
import modules.network.AppScope
import kotlin.coroutines.resume

@AppScope
@Inject
class InputDialogRepository(
) {
    var submit: (String) -> Unit by mutableStateOf({})
    var cancel: () -> Unit by mutableStateOf({})
    var inputTitle by mutableStateOf("")
    var inputDialogShow by mutableStateOf(false)
    var inputModel by mutableStateOf("")
    var keyboardType by mutableStateOf(KeyboardType.Text)

    suspend fun showInput(
        title: String,
        defaultValue: String = "",
        type: KeyboardType = KeyboardType.Text
    ): String {
        return suspendCancellableCoroutine {
            inputTitle = title
            keyboardType = type
            inputDialogShow = true
            inputModel = defaultValue
            submit = { result ->
                inputDialogShow = false
                if (it.isActive) {
                    it.resume(result)
                }
            }
            cancel = {
                inputDialogShow = false
                if (it.isActive) {
                    it.cancel()
                }
            }
        }
    }

    suspend fun inputAnd(
        title: String = "",
        type: KeyboardType = KeyboardType.Text,
        action: suspend (String) -> Unit
    ) {
        val result = showInput(title, type = type)
        action(result)
    }

}