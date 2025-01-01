@file:OptIn(ExperimentalResourceApi::class)

package view.page.activatePage

import aadenadmin.shared.generated.resources.Res
import aadenadmin.shared.generated.resources._CurrentHostIP
import aadenadmin.shared.generated.resources._EditHost
import aadenadmin.shared.generated.resources._NetworkError
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.ActionLeftMainButton
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@Composable
fun NetworkDownDisplay(
    changeIp: () -> Unit,
    ip: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(Res.string._NetworkError),
                    style = MaterialTheme.typography.titleMedium
                )
                GrowSpacer()
                Icon(imageVector = Icons.Default.WifiOff, contentDescription = null)
            }
            SmallSpacer()
            Text(stringResource(Res.string._CurrentHostIP) + ':' + ip)
            SmallSpacer(16)
            ActionLeftMainButton(text = stringResource(Res.string._EditHost), icon = Icons.Default.Wifi) {
                changeIp()
            }
        }

    }
}