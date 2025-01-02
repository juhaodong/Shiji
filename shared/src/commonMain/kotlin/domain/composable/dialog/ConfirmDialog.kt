package domain.composable.dialog

import LocalDialogManager
import shijiapp.shared.generated.resources.Res
import shijiapp.shared.generated.resources.tea_time
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.MainButton
import domain.composable.basic.layout.SmallSpacer
import domain.composable.dialog.basic.BeautifulDialog
import org.jetbrains.compose.resources.painterResource


@Composable
fun ConfirmDialog(

) {
    val manager = LocalDialogManager.current
    val state = manager.confirmDialogState
    if (manager.confirmDialog && state != null) {
        AlertDialog(
            onDismissRequest = { manager.confirmDialog = false },
            title = { Text(text = state.title) },
            text = { Text(text = state.content) },
            confirmButton = {
                TextButton(onClick = { state.action(); manager.confirmDialog = false }) {
                    Text(text = state.actionText)
                }
            }
        )
    }

}


@Composable
fun SuccessDialog(

) {
    val manager = LocalDialogManager.current
    val state = manager.successDialogState
    BeautifulDialog(
        manager.showSuccessDialog,
        onDismissRequest = {
            state?.action?.invoke()
            manager.showSuccessDialog = false
        },
    ) {
        if (state != null) {
            Column(
                modifier = Modifier.fillMaxWidth().height(400.dp).padding(36.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.tea_time),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
                SmallSpacer(16)
                Text(
                    state.title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                SmallSpacer()
                Text(
                    state.content,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            MainButton(state.actionText) {
                state.action()
                manager.showSuccessDialog = false
            }
        }

    }

}