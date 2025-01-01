package view.page.homePage.supplierManagePage.orders

import aadenadmin.shared.generated.resources.Res
import aadenadmin.shared.generated.resources.video_calling
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.MainButton
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import org.jetbrains.compose.resources.painterResource

@Composable
fun OrderSuccessPage(back: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.primary) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(36.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.video_calling),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    SmallSpacer(16)
                    Text(
                        "您的订单已经发送成功",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                    SmallSpacer()
                    Text(
                        "请等待供应商进行确认，确认的消息会第一时间发送给您，你也可以在App上，随时查看订单的状态。",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                }
            }
            BaseVCenterRow(modifier = Modifier.pa()) {
                MainButton("返回供应商首页", color = MaterialTheme.colorScheme.surface) {
                    back()
                }
            }
        }
    }

}