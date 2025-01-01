@file:OptIn(ExperimentalMaterial3Api::class)

package domain.composable.dialog

import aadenadmin.shared.generated.resources.Res
import aadenadmin.shared.generated.resources._OK
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import domain.composable.basic.button.ActionLeftMainButton
import domain.composable.basic.layout.SmallSpacer
import domain.composable.dialog.basic.BeautifulDialog
import domain.composable.dialog.basic.DialogViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun DateInputDialog(
    dialogViewModel: DialogViewModel
) {

    val datePickerState =
        rememberDatePickerState()

    BeautifulDialog(dialogViewModel.datePickerDialogShow, onDismissRequest = {
        dialogViewModel.datePickerDialogShow = false
    }) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        )
        SmallSpacer()
        ActionLeftMainButton(text = stringResource(Res.string._OK), icon = Icons.Default.Done) {
            if (datePickerState.selectedDateMillis != null) {
                dialogViewModel.submit(datePickerState.selectedDateMillis!!)
            }
        }
        SmallSpacer()
    }
}