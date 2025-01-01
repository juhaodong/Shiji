package dataLayer.serivce

import dataLayer.model.dish.AllergenModel
import dataLayer.model.dish.CategoryModel
import dataLayer.model.dish.DishesModel
import dataLayer.model.order.RequiredCategoryModel
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import modules.network.IKResponse


interface DishesService {
    @GET("Category.php?op=withConsumeType")
    suspend fun getCategories(
        @Query("lang") lang: String,
        @Query("consumeTypeId") consumeTypeId: Int
    ): IKResponse<List<CategoryModel>>

    @GET("Dishes.php?op=showAllAllergen")
    suspend fun showAllAllergen(): IKResponse<List<AllergenModel>>

    @GET("Dishes.php?onlyActive=0")
    suspend fun getAllDishes(@Query("lang") lang: String): IKResponse<List<DishesModel>>

    @GET("ConsumeType.php?op=showConsumeTypeCategoryRequire")
    suspend fun getRequiredCategory(@Query("consumeTypeId") consumeTypeId: Int): IKResponse<List<RequiredCategoryModel>>

}