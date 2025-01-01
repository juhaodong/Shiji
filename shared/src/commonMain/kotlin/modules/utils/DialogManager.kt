package modules.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class ConfirmDialogState(
    val title: String = "Attention!!",
    val content: String = "",
    val actionText: String = "OK",
    val action: () -> Unit = {},
)

class DialogManager(var coroutineScope: CoroutineScope? = null) {
    var confirmDialog by mutableStateOf(false)
    var confirmDialogState: ConfirmDialogState? by mutableStateOf(null)

    fun confirmWith(dialogState: ConfirmDialogState) {
        confirmDialogState = dialogState
        confirmDialog = true
    }

    fun confirmAnd(
        title: String = "您是否确认?",
        content: String = "该操作有一定的风险!",
        shouldConfirm: Boolean = true,
        action: () -> Unit = {}
    ) {
        if (shouldConfirm) {
            confirmWith(ConfirmDialogState(title = title, content = content, action = action))
        } else {
            action()
        }

    }

    var loadingDialog by mutableStateOf(false)
    fun loadingFor(action: suspend () -> Unit) {
        coroutineScope?.launch {
            loadingDialog = true
            action()
            loadingDialog = false
        }
    }

    var showSuccessDialog by mutableStateOf(false)
    var successDialogState: ConfirmDialogState? by mutableStateOf(null)
    fun successAnd(
        content: String = "大功告成，恭喜您!",
        title: String = "非常好！",
        action: () -> Unit = {}
    ) {

        successDialogState = (ConfirmDialogState(title = title, content = content, action = action))
        showSuccessDialog = true

    }


    fun confirmDelete(
        name: String,
        action: () -> Unit
    ) {
        confirmWith(
            ConfirmDialogState(
                title = "删除$name",
                content = "确定要删除吗？",
                actionText = "确定",
                action = action
            )
        )
    }


}

val globalDialogManager = DialogManager()