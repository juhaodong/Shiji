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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.raedghazal.kotlinx_datetime_ext.now
import domain.composable.basic.button.MainButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import domain.composable.dialog.basic.BeautifulDialog
import domain.user.IdentityVM
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import modules.utils.FormatUtils.displayWithUnit
import kotlin.toString

@Composable
fun ProfileDialog(identityVM: IdentityVM) {
    BeautifulDialog(identityVM.showProfileDialog, onDismissRequest = {
        identityVM.showProfileDialog = false
    }) {
        BaseCardHeader(
            icon = Icons.Rounded.CloudSync,
            title = "我的档案和目标",
            subtitle = "根据您填写的身高体重等数据自动计算。",
        )
        UserProfileView(identityVM = identityVM)
        BaseVCenterRow(modifier = Modifier.pa()) {
            MainButton(
                "修改目标", Icons.Default.Add, color = MaterialTheme.colorScheme.primary
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
        identityVM.updateProfile {
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
    if (profile != null) {
        val totalMinus = profile.currentWeight.toFloat() - profile.targetWeight.toFloat()

        val restDate = profile.weightLossCycle - LocalDate.now()
            .daysUntil(profile.startDate.plus(profile.weightLossCycle, DateTimeUnit.DAY))
        val currentWeight =
            profile.currentWeight.toFloat() - restDate / profile.weightLossCycle.toFloat() * totalMinus
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            ProfileRow("昵称", profile.nickname)
            ProfileRow("生日", profile.birthDate.toString(), isDate = true)
            ProfileRow("身高", profile.height.toString(), unit = "cm")
            ProfileRow("当前体重", profile.currentWeight.toString(), unit = "kg")
            ProfileRow("目标体重", profile.targetWeight.toString(), unit = "kg")
            ProfileRow("减重周期", profile.weightLossCycle.toString(), unit = "天")
            ProfileRow("本日目标体重", currentWeight.toBigDecimal().displayWithUnit(), unit = "天")
            ProfileRow("剩余日期", restDate.toString(), unit = "天")
            ProfileRow(
                "运动强度", when (profile.exerciseIntensity) {
                    1 -> ExerciseIntensity.LOW.displayName
                    2 -> ExerciseIntensity.MEDIUM.displayName
                    3 -> ExerciseIntensity.HIGH.displayName
                    else -> ""
                }
            )

        }
    }
}

@Composable
fun ProfileRow(
    label: String,
    value: String,
    unit: String? = null,
    isDate: Boolean = false,
    strong: Boolean = false
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (strong) MaterialTheme.colorScheme.primary else
            MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.3f
            ),
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Medium
            )
            if (isDate) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                )
            } else {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                )
                SmallSpacer()
                if (unit != null) {
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

        }
    }
}


enum class ExerciseIntensity(val displayName: String) {
    LOW("少量运动"), MEDIUM("中等运动"), HIGH("大量运动")
}