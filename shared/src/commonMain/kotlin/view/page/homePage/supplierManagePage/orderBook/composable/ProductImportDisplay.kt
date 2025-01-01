package view.page.homePage.supplierManagePage.orderBook.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.supplier.model.SupplierProduct
import modules.utils.FormatUtils.toPriceDisplay

@Composable
fun ProductImportDisplay(
    imported: Boolean,
    info: SupplierProduct,
    onClick: () -> Unit = {},
    toggle: () -> Unit = {},

    ) {
    Surface(onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp, vertical = 8.dp)) {
            Row {
                if (info.imageUrl.isNotBlank()) {
                    AsyncImage(
                        info.realImageUrl(),
                        contentDescription = null,
                        modifier = Modifier.size(84.dp)
                    )
                    SmallSpacer(16)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row {
                        Text(
                            info.name,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        SmallSpacer()
                        Text(
                            "~" + info.defaultPrice.toPriceDisplay(),
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        info.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

            }
            SmallSpacer()
            BaseVCenterRow {
                TextButton(onClick) {
                    Text("详细信息")
                }
                GrowSpacer()
                if (imported) {
                    BaseIconButton(
                        icon = Icons.Rounded.CheckCircle,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        toggle()
                    }
                } else {
                    OutlinedButton(onClick = toggle) {
                        Text("导入")
                    }
                }

            }
        }
    }
    HorizontalDivider()
}