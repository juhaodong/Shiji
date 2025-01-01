package domain.inventory

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import domain.inventory.model.DashboardDataDTO
import domain.inventory.model.InventorySetting
import domain.inventory.model.InventorySettingDTO
import domain.inventory.model.change.InventoryChangeDTO
import domain.inventory.model.change.InventoryChangeLogModel
import domain.inventory.model.change.OperationType
import domain.inventory.model.storageItem.PurchasePriceDTO
import domain.inventory.model.storageItem.ResourceCategoryModel
import domain.inventory.model.storageItem.ShopResourceDTO
import domain.inventory.model.storageItem.StorageItemModel
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import modules.network.cloudUrl

private const val BASE_URL = "$cloudUrl/inventory" // Or your actual base URL
private const val RESOURCE_URL = "$BASE_URL/resource"
private const val CHANGE_URL = "$BASE_URL/change"

interface InventoryService {


    @POST("$cloudUrl/uploadFile")
    suspend fun uploadFile(@Body map: MultiPartFormDataContent): String


    @GET("$BASE_URL/shop/info/{shopId}")
    suspend fun getInventorySetting(
        @Path("shopId") shopId: String,
    ): InventorySetting?


    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/shop/save")
    suspend fun saveSetting(
        @Body dto: InventorySettingDTO
    )

    @GET("$BASE_URL/shop/subscription/{shopId}")
    suspend fun getInventorySubscriptionStatus(
        @Path("shopId") shopId: String
    ): Boolean

    @GET("$BASE_URL/dashboard/info/{id}")
    suspend fun getDashboardInfo(@Path("id") shopId: Long): DashboardDataDTO

    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/resourceCategory/save")
    suspend fun saveResourceCategory(@Body dto: ResourceCategoryModel): ResourceCategoryModel

    @POST("$BASE_URL/resourceCategory/delete/{id}")
    suspend fun deleteResourceCategory(@Path("id") id: Long)

    @GET("$BASE_URL/resourceCategory/list/{shopId}")
    suspend fun getResourceCategoryList(@Path("shopId") shopId: Long): List<ResourceCategoryModel>

    @GET("$BASE_URL/resource/listByShopId/{shopId}")
    suspend fun getStorageItemList(@Path("shopId") shopId: Long): List<StorageItemModel>

    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/resource/save")
    suspend fun saveResource(@Body dto: ShopResourceDTO)

    @Headers("Content-Type: application/json")
    @POST("$RESOURCE_URL/purchasePrice/save")
    suspend fun savePurchasePrice(@Body dto: PurchasePriceDTO)

    @POST("$RESOURCE_URL/purchasePrice/delete/{id}")
    suspend fun deletePurchasePrice(@Path("id") id: Long)

    @POST("$RESOURCE_URL/delete/{id}")
    suspend fun deleteResource(@Path("id") id: Long)

    @GET("$RESOURCE_URL/listByShopId/{shopId}/{categoryId}")
    suspend fun getStorageItemListByCategoryId(
        @Path("shopId") shopId: Long,
        @Path("categoryId") categoryId: Long
    ): List<StorageItemModel>

    @Headers("Content-Type: application/json")
    @POST("$CHANGE_URL/enter")
    suspend fun enter(@Body dto: InventoryChangeDTO)

    @Headers("Content-Type: application/json")
    @POST("$CHANGE_URL/exit")
    suspend fun exit(@Body dto: InventoryChangeDTO)

    @GET("$CHANGE_URL/recent-operations/{storageItemId}")
    suspend fun recentOperations(@Path("storageItemId") storageItemId: Long): List<InventoryChangeLogModel>

    @GET("$CHANGE_URL/detail/{storageItemId}")
    suspend fun storageDetail(@Path("storageItemId") storageItemId: Long): StorageItemDetailDTO

    @Headers("Content-Type: application/json")
    @POST("$CHANGE_URL/list/{shopId}")
    suspend fun recentOperationsByShop(
        @Path("shopId") shopId: Long,
        @Body dto: DishResourceChangeLogSearchDto = DishResourceChangeLogSearchDto(null, null, null)
    ): List<InventoryChangeLogModel>

}

@Serializable
class StorageItemDetailDTO(
    val storageItem: StorageItemModel,
    val batches: List<StorageItemBatch>,
    val recentRecords: List<InventoryChangeLogModel>,
    @Contextual
    val lossAmount: BigDecimal,
    val lossUnitDisplay: String,
    @Contextual
    val totalOutAmount: BigDecimal,
    val totalOutUnitDisplay: String,
    @Contextual
    val otherAmount: BigDecimal,
    val otherUnitDisplay: String,
)

@Serializable
class StorageItemBatch(
    @Contextual
    var amount: BigDecimal,
    @Contextual
    val unitPrice: BigDecimal,
    var unitDisplay: String,
)

@Serializable
class DishResourceChangeLogSearchDto(
    val dateTimeFrom: LocalDateTime?,
    val dateTimeTo: LocalDateTime?,
    val operation: OperationType? = null,
)
