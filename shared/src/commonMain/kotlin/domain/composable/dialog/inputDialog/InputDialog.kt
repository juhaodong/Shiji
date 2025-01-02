package domain.composable.dialog.inputDialog

import shijiapp.shared.generated.resources.Res
import shijiapp.shared.generated.resources._OK
import shijiapp.shared.generated.resources._PleaseEnter
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import domain.composable.basic.button.ActionLeftMainButton
import domain.composable.basic.layout.SmallSpacer
import domain.composable.dialog.basic.BeautifulDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun InputDialog(inputDialogRepository: InputDialogRepository) {
    val focusRequester = remember { FocusRequester() }
    BeautifulDialog(inputDialogRepository.inputDialogShow, onDismissRequest = {
        inputDialogRepository.cancel()
    }) {

        Text(inputDialogRepository.inputTitle.ifBlank {
            stringResource(Res.string._PleaseEnter)
        }, style = MaterialTheme.typography.titleMedium)
        SmallSpacer()
        TextField(
            inputDialogRepository.inputModel,
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(keyboardType = inputDialogRepository.keyboardType),
            onValueChange = {
                inputDialogRepository.inputModel = it
            },
            singleLine = true
        )
        SmallSpacer()
        ActionLeftMainButton(text = stringResource(Res.string._OK), icon = Icons.Default.Done) {
            inputDialogRepository.submit(inputDialogRepository.inputModel)
        }
        SmallSpacer()
        LaunchedEffect(true) {
            focusRequester.requestFocus()
        }
    }
}