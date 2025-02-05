package view.page.homePage.workbenchPage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.cards.LabelText
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.dialog.basic.DialogViewModel
import domain.user.IdentityVM
import domain.user.NutritionVM
import org.jetbrains.compose.resources.painterResource
import shijiapp.shared.generated.resources.Res
import shijiapp.shared.generated.resources.no_image
import view.page.homePage.AppToolbarFragment


@Composable
fun WorkbenchPage(
    identityVM: IdentityVM,
    dialogViewModel: DialogViewModel,
    nutritionVM: NutritionVM,
    toAdminPage: () -> Unit,
    toTeamManagePage: () -> Unit,
    onChangeTheme: () -> Unit,
    onLogOut: () -> Unit
) {
    Column {
        AppToolbarFragment(identityVM, nutritionVM)
        HorizontalDivider()
        val initialMenuList =
            listOfNotNull(
                MenuModel.ActionMenuItem("目标管理") {
                    identityVM.showProfileDialog = true
                },
                MenuModel.ActionMenuItem("主题设置") { onChangeTheme() },
                MenuModel.ActionMenuItem("关于") { identityVM.showComingSoonDialog() },
                MenuModel.ActionMenuItem("删除账户") { identityVM.requestDeleteAccount() },
                MenuModel.ActionMenuItem("登出") { onLogOut() }
            )


        MenuList(
            menuItems = initialMenuList,
        )
    }


}


@Composable
private fun UserListDisplay(
    displayName: String?, userId: String, photoUrl: String?,
    onSetting: () -> Unit
) {
    BaseVCenterRow {
        AsyncImage(
            photoUrl,
            contentDescription = null,
            placeholder = painterResource(Res.drawable.no_image),
            error = painterResource(Res.drawable.no_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(36.dp).clip(MaterialTheme.shapes.medium)
        )
        SmallSpacer()
        Column {
            Text(
                displayName ?: "没有设置显示名称", style = MaterialTheme.typography.bodyMedium,
            )
            SmallSpacer(2)
            LabelText(userId, secondary = true)
        }
        GrowSpacer()
        SmallSpacer()
        BaseIconButton(Icons.Default.Settings) {
            onSetting()


        }
    }
}