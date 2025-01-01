@file:OptIn(ExperimentalMaterial3Api::class)

package view.page

import LocalDialogManager
import aadenadmin.shared.generated.resources.Res
import aadenadmin.shared.generated.resources.no_image
import aadenadmin.shared.generated.resources.stretching
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.InsertInvitation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.button.MainButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.cards.LabelText
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.py
import domain.composable.basic.wrapper.PageLoadingProvider
import domain.composable.dialog.basic.BeautifulDialog
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.selection.SelectOption
import domain.composable.filter.SearchTopBarProvider
import domain.user.IdentityVM
import domain.user.model.UserShopUserDTO
import domain.user.model.UserStoreAuth
import domain.user.model.selectableAuth
import modules.utils.isValidEmail
import modules.utils.timeToNow
import org.jetbrains.compose.resources.painterResource

@Composable
fun TeamManagePage(identityVM: IdentityVM, dialogViewModel: DialogViewModel, back: () -> Unit) {
    val manager = LocalDialogManager.current
    Scaffold(topBar = {
        SearchTopBarProvider(searching = identityVM.userListSearching,
            searchText = identityVM.userListSearchText,
            onSearchTextChange = {
                identityVM.userListSearchText = it
            },
            dismiss = {
                identityVM.userListSearching = false
            },
            content = {
                TopAppBar(
                    title = {
                        Text("成员列表", style = MaterialTheme.typography.titleSmall)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            back()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            identityVM.userListSearching = true
                        }) {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                        identityVM.displayWithAuth(UserStoreAuth.Owner) {
                            IconButton(onClick = {
                                identityVM.startInvite()
                            }) {
                                Icon(Icons.Default.AddCircle, contentDescription = null)
                            }
                        }

                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                )
            })
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            PageLoadingProvider(
                loading = identityVM.userListLoading,
                refreshKey = identityVM.currentStore,
                onRefresh = { identityVM.refreshUserList() },
                haveContent = identityVM.userList.isNotEmpty()
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(identityVM.filteredUserList()) {
                        UserListDisplay(
                            it
                        ) {
                            if (identityVM.haveAuth(UserStoreAuth.Owner)) {
                                dialogViewModel.runInScope {
                                    val normalOptions = listOf(
                                        SelectOption("🚫 移除用户", "移除用户"),
                                        SelectOption("🔑 更新权限", "更新权限"),
                                    )

                                    val notConfirmedOptions = listOf(
                                        SelectOption("🗑️ 删除邀请", "删除邀请"),
                                        SelectOption("✉️ 重新发送邀请邮件", "重新发送邀请邮件"),
                                        SelectOption("⏱️ 延长邀请时间", "延长邀请时间")
                                    )
                                    val selectedOption = dialogViewModel.showSelectDialog(
                                        "请选择操作", if (it.firebaseUid.isNotBlank())
                                            normalOptions else notConfirmedOptions,
                                        twoRow = true
                                    )
                                    when (selectedOption) {
                                        "移除用户" -> {
                                            if (it.isOwner) {
                                                manager.confirmAnd(
                                                    "请万分小心！！",
                                                    "您正在移除主用户。一旦您移除了主用户，那么您再重新绑定设备前，将无法再邀请其他用户，您目前团队内的其他用户将不受影响"
                                                ) {
                                                    identityVM.removeUser(uid = it.firebaseUid)
                                                }
                                            } else {
                                                manager.confirmAnd(
                                                    "您是否要移除此用户?",
                                                    "移除后，该用户将无法继续使用本应用管理您的门店数据"
                                                ) {
                                                    identityVM.removeUser(uid = it.firebaseUid)
                                                }
                                            }

                                        }

                                        "更新权限" -> {
                                            if (it.isOwner) {
                                                manager.confirmAnd(
                                                    "您无法设置主用户的权限",
                                                    "主用户默认具有所有权限，无法设置其权限"
                                                )
                                            } else {
                                                identityVM.startUpdateAuth(it.firebaseUid)
                                            }
                                        }

                                        "删除邀请" -> {
                                            // Handle deleting the invitation
                                            manager.confirmDelete("邀请") {
                                                it.activeCode?.let { it1 ->
                                                    identityVM.deleteInvite(
                                                        it1
                                                    )
                                                }
                                            }

                                        }

                                        "重新发送邀请邮件" -> {
                                            // Handle resending the invitation email
                                            manager.confirmAnd(
                                                "您是否确定要重新发送邀请邮件?",
                                                "在重新发送之前，请确保您已经检查了垃圾邮件。"
                                            ) {
                                                it.activeCode?.let { it1 ->
                                                    identityVM.refreshInvite(
                                                        it1
                                                    )
                                                }
                                            }

                                        }

                                        "延长邀请时间" -> {
                                            manager.confirmAnd(
                                                "您是否要延长此邀请的生效时间?",
                                                "每次延长都会增加14天的生效时间。如果邀请失效，您可以重新创建邀请。"
                                            ) {
                                                it.activeCode?.let { it1 ->
                                                    identityVM.refreshInvite(
                                                        it1
                                                    )
                                                }
                                            }
                                        }

                                        else -> {
                                            // Handle other cases or no selection
                                        }
                                    }
                                }
                            } else {
                                manager.confirmAnd("不好意思哦", "您没有权限管理这里的成员")
                            }

                        }
                    }
                }
            }
        }

        BeautifulDialog(show = identityVM.showUpdateAuthDialog, onDismissRequest = {
            identityVM.showUpdateAuthDialog = false
        }, loading = identityVM.inviteLoading) {
            BaseCardHeader(
                "更新权限",
                "更新用户权限",
                noPadding = true
            )
            SmallSpacer(32)
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        "权限选择",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black
                    )
                    SmallSpacer(4)
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp).padding(start = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            selectableAuth.forEach {
                                BaseVCenterRow(modifier = Modifier) {
                                    Text(it.name, style = MaterialTheme.typography.bodyLarge)
                                    GrowSpacer()
                                    Checkbox(identityVM.selectedAuth.contains(it), { boo ->
                                        if (boo) {
                                            identityVM.selectedAuth.add(it)
                                        } else {
                                            identityVM.selectedAuth.remove(it)
                                        }
                                    })
                                }
                            }
                        }

                    }

                }

                MainButton("确认更新") {
                    identityVM.submitAuthChange()
                }

            }
        }

        BeautifulDialog(show = identityVM.showCreateInviteDialog, onDismissRequest = {
            identityVM.showCreateInviteDialog = false
        }, loading = identityVM.inviteLoading) {
            BaseCardHeader(
                "创建邀请",
                "邀请其他用户加入门店",
                noPadding = true
            )
            SmallSpacer(32)
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        "权限选择",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black
                    )
                    SmallSpacer(4)
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp).padding(start = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            selectableAuth.forEach {
                                BaseVCenterRow(modifier = Modifier) {
                                    Text(it.name, style = MaterialTheme.typography.bodyLarge)
                                    GrowSpacer()
                                    Checkbox(identityVM.selectedAuth.contains(it), { boo ->
                                        if (boo) {
                                            identityVM.selectedAuth.add(it)
                                        } else {
                                            identityVM.selectedAuth.remove(it)
                                        }
                                    })
                                }
                            }
                        }

                    }

                }

                TextField(singleLine = true, value = identityVM.emailInput, onValueChange = {
                    identityVM.emailInput = it
                }, label = {
                    Text("请输入邀请对象的邮箱")
                }, isError = !identityVM.emailInput.isValidEmail(),
                    trailingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                MainButton("发送邀请") {
                    identityVM.submitInvite()
                }

            }
        }
    }
}

@Composable
private fun UserListDisplay(
    userInfo: UserShopUserDTO, onSetting: () -> Unit
) {
    val exist = userInfo.firebaseUid.isNotBlank()
    BaseVCenterRow {
        AsyncImage(
            userInfo.photoUrl,
            contentDescription = null,
            placeholder = painterResource(Res.drawable.no_image),
            error = painterResource(Res.drawable.no_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(36.dp).clip(MaterialTheme.shapes.medium)
        )
        SmallSpacer()
        Column {
            if (exist) {
                Text(
                    (userInfo.displayName
                        ?: "没有设置显示名称") + (if (userInfo.isOwner) "（主用户）" else ""),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                SmallSpacer(2)
                LabelText(
                    userInfo.email.orEmpty().ifBlank { userInfo.firebaseUid }, secondary = true
                )
            } else {
                Text(
                    userInfo.email ?: "没有设置邮箱",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (userInfo.isOwner) FontWeight.Black else FontWeight.Normal
                )
                SmallSpacer(2)
                LabelText(
                    "邀请已发送，激活码为" + userInfo.activeCode,
                    secondary = true
                )
            }

        }
        GrowSpacer()
        SmallSpacer()
        BaseIconButton(Icons.Default.Settings) {
            onSetting()
        }


    }
}