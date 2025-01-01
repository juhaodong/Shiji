package domain.composable.dialog.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import domain.composable.basic.TwoItemsPerRowGrid
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.pa
import domain.composable.dialog.basic.BeautifulDialog
import domain.composable.dialog.basic.DialogViewModel


@Composable
fun SimpleSelectionDialog(
    dialogViewModel: DialogViewModel,
) {
    BeautifulDialog(
        show = dialogViewModel.showDialog,
        onDismissRequest = { dialogViewModel.showDialog = false },
        noPadding = true
    ) {
        BaseCardHeader(
            title = dialogViewModel.title,
            subtitle = "在下方的选项中选择一个",
            icon = Icons.Default.Link
        )

        if (dialogViewModel.twoRow) {
            TwoItemsPerRowGrid(
                dialogViewModel.selectOptions,
                horizontalSpacing = 8.dp,
                verticalSpacing = 8.dp
            ) {
                BaseSurface(
                    onClick = {
                        it.value?.let { dialogViewModel.confirmCallBack?.let { it1 -> it1(it) } }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = it.label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Unspecified
                        )
                    }

                }
            }
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    dialogViewModel.selectOptions.filter {
                        dialogViewModel.searchText.isBlank() || it.label.contains(dialogViewModel.searchText)
                    }.forEach { option ->
                        Surface(
                            onClick = {
                                option.value?.let {
                                    dialogViewModel.confirmCallBack?.let { it1 ->
                                        it1(
                                            it
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Unspecified

                                )
                            }

                        }
                    }
                }

            }
        }
        if (!dialogViewModel.twoRow && dialogViewModel.selectOptions.size > 8) {
            BaseSurface(color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                val textField = FocusRequester()
                BaseVCenterRow(modifier = Modifier.pa()) {

                    OutlinedTextField(
                        dialogViewModel.searchText,
                        onValueChange = { dialogViewModel.searchText = it },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().focusRequester(textField),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        placeholder = {
                            Text("请输入关键字搜索..")
                        },
                    )
                }
                LaunchedEffect(true) {
                    textField.requestFocus()
                }

            }

        }
    }
}