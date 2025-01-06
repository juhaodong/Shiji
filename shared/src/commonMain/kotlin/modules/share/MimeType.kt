package modules.share

import androidx.compose.runtime.Composable

enum class MimeType {
    PDF,
    TEXT,
    IMAGE,
}

class ShareFileModel(
    val mime: MimeType = MimeType.PDF,
    val fileName: String,
    val bytes: ByteArray
)


expect class ShareManager {
    fun shareText(text: String)
    suspend fun shareFile(file: ShareFileModel): Result<Unit>
}

@Composable
expect fun rememberShareManager(): ShareManager