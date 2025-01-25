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
import androidx.compose.foundation.layout.requiredSizeIn
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
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.Permission
import com.mohamedrejeb.calf.permissions.isGranted
import com.mohamedrejeb.calf.permissions.rememberPermissionState
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import dev.icerock.moko.media.compose.BindMediaPickerEffect
import dev.icerock.moko.media.compose.rememberMediaPickerControllerFactory
import dev.icerock.moko.media.picker.MediaSource
import domain.composable.basic.button.BaseIcon
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
import domain.composable.basic.layout.px
import domain.composable.basic.wrapper.PageLoadingProvider
import domain.composable.dialog.basic.BeautifulDialog
import domain.food.service.PlateSize
import domain.food.service.PlateSizeSelector
import domain.user.IdentityVM
import domain.user.NutritionVM
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import modules.utils.FormatUtils.displayWithUnit
import modules.utils.imageWithProxy
import modules.utils.timeToNow
import org.jetbrains.compose.resources.painterResource
import shijiapp.shared.generated.resources.Res
import shijiapp.shared.generated.resources.hourglass


@Composable
fun RecordPage(
    identityVM: IdentityVM, nutritionVM: NutritionVM
) {
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var imageByteArray: ByteArray? by remember { mutableStateOf(null) }
    var personCount by remember { mutableStateOf(1) }
    var minPlateSize by remember { mutableStateOf(PlateSize.MEDIUM) }
    val cameraPermissionState = rememberPermissionState(
        Permission.Camera
    )
    val hapticFeedback = LocalHapticFeedback.current

    val factory = rememberMediaPickerControllerFactory()
    val picker = remember(factory) { factory.createMediaPickerController() }

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single, scope = scope, onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                imageByteArray = it
            }
        })
    BindMediaPickerEffect(picker)

    LaunchedEffect(true) {
        nutritionVM.refreshFoodLog()
    }

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
                                SubcomposeAsyncImage(
                                    model = it.imageUrl,
                                    contentDescription = null,
                                    loading = {
                                        Column(modifier = Modifier.fillMaxWidth().height(128.dp)) {
                                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                        }

                                    },
                                    modifier = Modifier.fillMaxWidth()
                                        .requiredSizeIn(minHeight = 64.dp).clip(
                                            MaterialTheme.shapes.large
                                        ),
                                    contentScale = ContentScale.FillWidth
                                )
                                Column(modifier = Modifier.pa(8)) {
                                    Text(
                                        it.socialDescription,
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
        val bitmap = imageByteArray?.toImageBitmap()
        BaseSurface() {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

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


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                scope.launch {
                                    val result = runCatching {
                                        picker.pickImage(MediaSource.CAMERA)
                                    }

                                    imageByteArray = result.getOrNull()?.toByteArray()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().pa(16),
                                contentAlignment = Alignment.Center
                            ) {
                                BaseIcon(icon = Icons.Default.PhotoCamera, size = 28)
                            }
                        }
                        Surface(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                singleImagePicker.launch()
                            },
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().pa(16),
                                contentAlignment = Alignment.Center
                            ) {
                                BaseIcon(icon = Icons.Default.AddPhotoAlternate, size = 28)
                            }
                        }
                    }

                }
            }
        }
        SmallSpacer()
        if (bitmap != null) {
            PlateSizeSelector(minPlateSize) {
                minPlateSize = it
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
            nutritionVM.foodLogLoading = true
            CoroutineScope(Dispatchers.IO).launch {
                nutritionVM.createFoodLog(
                    personCount,
                    imageByteArray!!,
                    minPlateSize.approximateDiameterCm
                )
                showDialog = false
            }

        }
    }

}
