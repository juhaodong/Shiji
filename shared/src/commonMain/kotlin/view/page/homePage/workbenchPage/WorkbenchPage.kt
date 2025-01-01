package view.page.homePage.workbenchPage

import LocalDialogManager
import aadenadmin.shared.generated.resources.Res
import aadenadmin.shared.generated.resources.no_image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.cards.LabelText
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.selection.SelectOption
import domain.user.IdentityVM
import domain.user.model.UserStoreAuth
import org.jetbrains.compose.resources.painterResource
import view.page.homePage.dataCenterPage.storeList.StoreList


@Composable
fun WorkbenchPage(
    identityVM: IdentityVM,
    dialogViewModel: DialogViewModel,
    toAdminPage: () -> Unit,
    toTeamManagePage: () -> Unit,
    onChangeTheme: () -> Unit,
    onLogOut: () -> Unit
) {
    val user = identityVM.currentUser ?: return
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val manager = LocalDialogManager.current
    val singleImagePicker = rememberImagePickerLauncher(selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                identityVM.updateImage(it)
            }
        })
    Column {
        StoreList(identityVM)
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
            SmallSpacer()
            UserListDisplay(
                displayName = user.displayName,
                userId = user.email ?: user.uid,
                photoUrl = user.photoURL
            ) {
                dialogViewModel.runInScope {
                    val change = dialogViewModel.showSelectDialog(
                        "请选择要设置的内容", listOf(
                            SelectOption("昵称 📝", "昵称"),
                            SelectOption("头像 🖼️", "头像"),
                        )
                    )
                    if (change == "昵称") {
                        identityVM.updateUserName()
                    } else {
                        singleImagePicker.launch()
                    }
                }
            }
        }
        HorizontalDivider()
        val initialMenuList =
            listOfNotNull(
                MenuModel.ActionMenuItem("门店管理") {
                    identityVM.showStoreManagementDialog = true
                },
                identityVM.withAuth(UserStoreAuth.Admin) {
                    MenuModel.ActionMenuItem("工作台") { toAdminPage() }
                },
                MenuModel.ActionMenuItem("组织管理") { toTeamManagePage() },
                MenuModel.ActionMenuItem("主题设置") { onChangeTheme() },
                MenuModel.ActionMenuItem("关于") { identityVM.showComingSoonDialog() },
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