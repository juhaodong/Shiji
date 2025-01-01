@file:OptIn(
    ExperimentalMaterial3Api::class
)

package domain.composable.dialog.basic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.composable.basic.wrapper.LoadingProvider

@Composable
fun BeautifulDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    noPadding: Boolean = false,
    useCloseButton: Boolean = false,
    loading: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
                confirmValueChange = {
                    if (it == SheetValue.Hidden) {
                        !useCloseButton
                    } else {
                        true
                    }
                }),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(if (noPadding) 0.dp else 12.dp)
            ) {
                LoadingProvider(loading) {
                    content()
                }

            }

        }
    }
}