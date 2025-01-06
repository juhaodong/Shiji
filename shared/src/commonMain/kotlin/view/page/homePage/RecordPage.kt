@file:OptIn(ExperimentalPermissionsApi::class)

package view.page.homePage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.Permission
import com.mohamedrejeb.calf.permissions.isGranted
import com.mohamedrejeb.calf.permissions.rememberPermissionState
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.button.BaseOutlinedIconButton
import domain.composable.basic.button.BaseTonalIconButton
import domain.composable.basic.button.MainButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import domain.composable.basic.wrapper.PageLoadingProvider
import domain.composable.dialog.basic.BeautifulDialog
import domain.user.IdentityVM
import domain.user.NutritionVM
import kotlinx.coroutines.launch
import modules.utils.FormatUtils.displayWithUnit
import modules.utils.imageWithProxy
import modules.utils.timeToNow


@Composable
fun RecordPage(
    identityVM: IdentityVM, nutritionVM: NutritionVM
) {
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var imageByteArray: ByteArray? by remember { mutableStateOf(null) }
    var personCount by remember { mutableStateOf(1) }
    val cameraPermissionState = rememberPermissionState(
        Permission.Camera
    )
    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(true) {
        nutritionVM.refreshFoodLog()
    }

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single, scope = scope, onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                imageByteArray = it
            }
        })
    Column(modifier = Modifier.fillMaxSize()) {
        AppToolbarFragment(identityVM = identityVM, nutritionVM)
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            PageLoadingProvider(
                nutritionVM.foodLogLoading,
                haveContent = nutritionVM.foodLogList.isNotEmpty(),
                onRefresh = {
                    scope.launch {
                        nutritionVM.refreshFoodLog()

                    }
                }) {

                SmallSpacer(16)
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalItemSpacing = 4.dp
                ) {
                    items(nutritionVM.foodLogList) {
                        BaseSurface(
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            onClick = {
                                nutritionVM.showFoodLog(it)
                            }) {
                            Column(modifier = Modifier) {
                                AsyncImage(
                                    it.imageUrl.imageWithProxy(),
                                    null,
                                    modifier = Modifier.fillMaxWidth().clip(
                                        MaterialTheme.shapes.large
                                    ),
                                    contentScale = ContentScale.FillWidth
                                )
                                Column(modifier = Modifier.pa(8)) {
                                    Text(
                                        it.foodDescription,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    SmallSpacer()
                                    BaseVCenterRow {
                                        Icon(
                                            Icons.Default.LocalFireDepartment,
                                            null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(16.dp).clip(
                                                MaterialTheme.shapes.extraLarge
                                            ).background(MaterialTheme.colorScheme.primary)
                                                .padding(1.dp)

                                        )
                                        SmallSpacer(2)
                                        Text(
                                            it.calories.displayWithUnit("Kcal"),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        GrowSpacer()
                                        Text(
                                            it.createTimestamp.timeToNow(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }


                            }
                        }
                    }
                }
            }

            MainButton(
                "添上一笔",
                color = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.PhotoCamera
            ) {
                if (cameraPermissionState.status.isGranted) {
                    showDialog = true
                } else {
                    cameraPermissionState.launchPermissionRequest()
                }

            }
            SmallSpacer()
        }

    }

    BeautifulDialog(showDialog, onDismissRequest = {
        showDialog = false
    }) {
        var hintText by remember {
            mutableStateOf("请上传关于食物的照片，务必拍下全貌，这样子Ai才能更好的分析食物的营养成分")
        }
        LaunchedEffect(imageByteArray) {
            if (imageByteArray != null) {
                hintText = "非常好，图片看上去很清楚！"
            } else {
                hintText = "请上传关于食物的照片，务必拍下全貌，这样子Ai才能更好的分析食物的营养成分"
            }
        }
        LaunchedEffect(showDialog) {
            imageByteArray = null
        }
        BaseCardHeader(
            "纪录新的一餐",
            hintText,
            noPadding = true,
            icon = Icons.AutoMirrored.Filled.ListAlt
        )
        SmallSpacer(16)
        BaseSurface() {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val bitmap = imageByteArray?.toImageBitmap()
                if (bitmap != null) {
                    Image(
                        bitmap,
                        contentDescription = null,
                        modifier = Modifier.requiredHeightIn(max = 300.dp).wrapContentSize()
                            .clip(MaterialTheme.shapes.large).clickable {
                                imageByteArray = null
                            },
                        contentScale = ContentScale.Fit
                    )
                } else {
                    val state = rememberPeekabooCameraState(onCapture = {
                        imageByteArray = it
                    })

                    Box(modifier = Modifier.fillMaxWidth().wrapContentSize()) {
                        PeekabooCamera(
                            state = state,
                            modifier = Modifier.fillMaxWidth().aspectRatio(3 / 4f)
                                .requiredHeightIn(max = 500.dp),
                            permissionDeniedContent = {
                                Text(
                                    "您拒绝了相机使用的权限，" +
                                            "因此，您只能从下方选取相册内的图片，" +
                                            "或者，您可以重新在设置里授予相关权限"
                                )
                            },
                        )
                        Row(
                            modifier = Modifier.pa().fillMaxWidth().align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            BaseIconButton(icon = Icons.Default.PhotoCamera, color = Color.White) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                state.capture()
                            }
                            BaseIconButton(
                                icon = Icons.Default.AddPhotoAlternate,
                                color = Color.White
                            ) {
                                singleImagePicker.launch()
                            }
                        }
                    }


                }
            }
        }
        SmallSpacer(16)
        val hapticFeedback: HapticFeedback = LocalHapticFeedback.current
        BaseSurface {
            Column(modifier = Modifier.pa()) {
                Text("用餐人数", style = MaterialTheme.typography.bodyMedium)
                BaseVCenterRow() {
                    Text(personCount.toString())
                    GrowSpacer()
                    if (personCount > 1) {
                        BaseOutlinedIconButton(
                            icon = Icons.Default.Remove, modifier = Modifier.size(32.dp)
                        ) {
                            personCount--
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        SmallSpacer()
                    }

                    BaseTonalIconButton(icon = Icons.Default.Add, modifier = Modifier.size(32.dp)) {
                        personCount++
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }
            }
        }

        SmallSpacer(24)
        MainButton(
            "我记好了", icon = Icons.Default.Check, loading = nutritionVM.foodLogLoading
        ) {
            scope.launch {
                nutritionVM.createFoodLog(personCount, imageByteArray!!)
                showDialog = false
            }

        }
    }

}
