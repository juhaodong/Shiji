package domain.composable.filter

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.px
import domain.composable.basic.layout.py

@Composable
fun SearchTopBarProvider(
    searching: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    dismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (!searching) {
        content()
    } else {
        val textField = FocusRequester()
        Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh) {
            BaseVCenterRow(modifier = Modifier.px(16).py(8)) {
                OutlinedTextField(
                    searchText,
                    onValueChange = onSearchTextChange,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().focusRequester(textField),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    placeholder = {
                        Text("请输入关键字搜索..")
                    },
                    trailingIcon = {
                        BaseIconButton(icon = Icons.Default.Close) {
                            dismiss()
                        }
                    }
                )
            }
        }
        LaunchedEffect(searching) {
            textField.requestFocus()
        }

    }
}