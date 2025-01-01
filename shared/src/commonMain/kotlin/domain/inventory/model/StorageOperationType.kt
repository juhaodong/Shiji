package domain.inventory.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Input
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.ui.graphics.vector.ImageVector

enum class StorageOperationType {
    Enter,
    Out,
    Check,
    Loss
}

fun getStorageOperationLabelAndIcon(type: StorageOperationType): Pair<String, ImageVector> {
    return when (type) {
        StorageOperationType.Enter -> "入库" to Icons.AutoMirrored.Filled.Input
        StorageOperationType.Out -> "出库" to Icons.Filled.Output
        StorageOperationType.Loss -> "报损" to Icons.Filled.ReportProblem
        StorageOperationType.Check -> "盘点" to Icons.Filled.Edit
    }
}