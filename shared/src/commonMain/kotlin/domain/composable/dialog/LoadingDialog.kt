package domain.composable.dialog

import LocalDialogManager
import androidx.compose.runtime.Composable
import domain.composable.basic.wrapper.LoadingIndicator
import domain.composable.dialog.basic.BeautifulDialog
import domain.composable.dialog.basic.DialogViewModel

@Composable
fun LoadingDialog() {
    val manager = LocalDialogManager.current
    BeautifulDialog(manager.loadingDialog, onDismissRequest = {

    }, useCloseButton = true) {
        LoadingIndicator()
    }
}