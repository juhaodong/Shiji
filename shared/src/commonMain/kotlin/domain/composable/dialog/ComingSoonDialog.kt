package domain.composable.dialog

import aadenadmin.shared.generated.resources.Res
import aadenadmin.shared.generated.resources.stretching
import aadenadmin.shared.generated.resources.tea_time
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import domain.composable.basic.layout.SmallSpacer
import domain.composable.dialog.basic.BeautifulDialog
import domain.user.IdentityVM
import org.jetbrains.compose.resources.imageResource

@Composable
fun ComingSoonDialog(identityVM: IdentityVM) {
    BeautifulDialog(identityVM.showComingSoon, onDismissRequest = {
        identityVM.showComingSoon = false
    }) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SmallSpacer(64)
            Image(imageResource(Res.drawable.tea_time), null, modifier = Modifier.fillMaxWidth())
            SmallSpacer(32)
            Text("敬请期待", style = MaterialTheme.typography.titleLarge)
            SmallSpacer(4)
            Text("我们正在努力开发中！", style = MaterialTheme.typography.bodyMedium)
            SmallSpacer(32)
            FilledTonalButton(onClick = {
                identityVM.showComingSoon = false
            }) {
                Icon(imageVector = Icons.Default.ThumbUp, contentDescription = null)
                SmallSpacer()
                Text("好的")
            }
        }


    }
}

@Composable
fun OfflineDialog(identityVM: IdentityVM) {
    BeautifulDialog(identityVM.currentlyOffline, onDismissRequest = {
        identityVM.currentlyOffline = false
    }) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SmallSpacer(64)
            Image(imageResource(Res.drawable.stretching), null, modifier = Modifier.fillMaxWidth())
            SmallSpacer(32)
            Text("非常抱歉😟", style = MaterialTheme.typography.titleLarge)
            SmallSpacer(4)
            Text(
                "本时间段的数据尚未与ACS同步。" +
                        (if (identityVM.currentStore?.ngrokOnline == true) "您可能选择了较长时间段的数据，这些数据需要一段时间跟ACS同步，请稍后再试。"
                        else "您的门店内的机器似乎不在线，因此我们没有办法自动同步这一时间段的数据，请在门店内的机器在线后重新尝试。"),
                style = MaterialTheme.typography.bodyMedium
            )
            SmallSpacer(32)
            FilledTonalButton(onClick = {
                identityVM.currentlyOffline = false
            }) {
                Icon(imageVector = Icons.Default.ThumbUp, contentDescription = null)
                SmallSpacer()
                Text("好的")
            }
        }


    }
}