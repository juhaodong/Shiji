package dataLayer.model.dish

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.serialization.Serializable

@Serializable
data class CategoryModel(
    var name: String = "",
    val id: Int,
    var dishesCategoryTypeId: Int,
    val dcImage: String? = null,
    val desc: String? = null,
    val dishes: List<DishesModel>
) {
    var imageBitmap: ImageBitmap? = null
}



