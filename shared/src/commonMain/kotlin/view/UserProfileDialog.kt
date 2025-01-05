package view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.outlined.LineStyle
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.MainButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import domain.composable.dialog.basic.BeautifulDialog
import domain.user.IdentityVM

@Composable
fun StoreManagementDialog(identityVM: IdentityVM) {
    BeautifulDialog(identityVM.showProfileDialog, onDismissRequest = {
        identityVM.showProfileDialog = false
    }) {
        BaseCardHeader(
            title = "请选择您要查看数据的门店",
            subtitle = "您还可以添加更多的门店",
            icon = Icons.Default.Link
        )
        BaseVCenterRow(modifier = Modifier.pa()) {
            MainButton(
                "添加门店",
                Icons.Default.Add,
                color = MaterialTheme.colorScheme.primary
            ) {
                identityVM.updateProfile {

                }
            }
        }


    }

}


@Composable
fun ColumnScope.UserProfileFragment(identityVM: IdentityVM, profileUpdated: () -> Unit) {
    BaseCardHeader(
        icon = Icons.Rounded.CloudSync,
        title = "非常欢迎",
        subtitle = "请填写您的一些基本信息，以便我们更好的为您分析您的膳食需求",
        noPadding = true,
    )
    SmallSpacer(16)
    if (identityVM.currentProfile == null) {
        // Empty state
        Box(
            modifier = Modifier.fillMaxSize().weight(1f), // Take up remaining space
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.LineStyle, // Or any relevant icon
                    contentDescription = "No stores",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "您还没有填写用户的基本信息",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    } else {
        UserProfileView(identityVM = identityVM)
        GrowSpacer()
    }
    SmallSpacer(16)


    MainButton("马上填写", icon = Icons.Default.Add) {
        identityVM.updateProfile{
            profileUpdated()
        }
    }
    SmallSpacer(16)
    MainButton(
        "登出",
        icon = Icons.AutoMirrored.Filled.Logout,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        identityVM.logout()
    }
}


@Composable
fun UserProfileView(identityVM: IdentityVM) {
    val profile = identityVM.currentProfile

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        ProfileRow("昵称", profile?.nickname ?: "")
        ProfileRow("生日", profile?.birthDate?.toString() ?: "")
        ProfileRow("身高", profile?.height?.toString() ?: "")
        ProfileRow("当前体重", profile?.currentWeight?.toString() ?: "")
        ProfileRow("目标体重", profile?.targetWeight?.toString() ?: "")
        ProfileRow("减重周期", profile?.weightLossCycle?.toString() ?: "")
        ProfileRow("运动强度", profile?.exerciseIntensity?.toString() ?: "")
    }
}

@Composable
fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(2f)
        )
    }
}