@file:OptIn(ExperimentalUuidApi::class)

package domain.inventory

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import domain.inventory.model.DashboardDataDTO
import domain.inventory.model.InventorySetting
import domain.inventory.model.InventorySettingDTO
import domain.inventory.model.change.InventoryChangeLogModel
import domain.inventory.model.change.InventoryCorrectModel
import domain.inventory.model.change.InventoryEnterModel
import domain.inventory.model.change.InventoryExitModel
import domain.inventory.model.change.toDTO
import domain.inventory.model.storageItem.PurchasePriceDTO
import domain.inventory.model.storageItem.ResourceCategoryModel
import domain.inventory.model.storageItem.ShopResourceDTO
import domain.inventory.model.storageItem.StorageItemModel
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.SafeRequestScope
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@AppScope
class InventoryRepository @Inject constructor(
    private val inventoryService: InventoryService,
    val globalSettingManager: GlobalSettingManager,
    val json: Json
) {

    suspend fun uploadFile(byteArray: ByteArray): String? {


        return SafeRequestScope.handleRequest {
            val multipart = MultiPartFormDataContent(formData {
                append("description", "Image")
                append("file", byteArray, Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(
                        HttpHeaders.ContentDisposition,
                        "filename=" + Uuid.random().toHexString() + ".png"
                    )
                })
            })

            inventoryService.uploadFile(multipart)
        }
    }


    suspend fun getInventorySetting(shopId: String): InventorySetting? {
        return SafeRequestScope.handleRequest { inventoryService.getInventorySetting(shopId) }
    }

    suspend fun saveSetting(dto: InventorySettingDTO) {
        SafeRequestScope.handleRequestWithError {
            inventoryService.saveSetting(dto)
        }
    }

    suspend fun getInventorySubscriptionStatus(shopId: String): Boolean {
        return SafeRequestScope.handleRequest {
            inventoryService.getInventorySubscriptionStatus(
                shopId
            )
        } == true
    }

    suspend fun getDashboardInfo(id: Long): DashboardDataDTO? {
        return SafeRequestScope.handleRequest { inventoryService.getDashboardInfo(id) }
    }


    suspend fun saveResourceCategory(dto: ResourceCategoryModel): ResourceCategoryModel {
        return SafeRequestScope.handleRequestWithError {
            inventoryService.saveResourceCategory(dto)
        }
    }

    suspend fun deleteResourceCategory(id: Long) {
        SafeRequestScope.handleRequestWithError {
            inventoryService.deleteResourceCategory(id)
        }

    }

    suspend fun getStorageItemList(activeCategoryId: Long? = null): List<StorageItemModel> {

        return SafeRequestScope.handleRequest {
            if (activeCategoryId != null) {
                inventoryService.getStorageItemListByCategoryId(
                    globalSettingManager.selectedDeviceId.toLong(), activeCategoryId
                )
            } else {
                inventoryService.getStorageItemList(globalSettingManager.selectedDeviceId.toLong())
            }

        } ?: emptyList()
    }

    suspend fun getResourceCategoryList(): List<ResourceCategoryModel> {
        return inventoryService.getResourceCategoryList(globalSettingManager.selectedDeviceId.toLong())
    }

    suspend fun saveResource(dto: ShopResourceDTO) {
        SafeRequestScope.handleRequestWithError {
            inventoryService.saveResource(dto)
        }
    }


    suspend fun savePurchasePrice(dto: PurchasePriceDTO) {
        SafeRequestScope.handleRequestWithError {
            inventoryService.savePurchasePrice(dto)
        }
    }

    suspend fun deletePurchasePrice(id: Long) {
        SafeRequestScope.handleRequestWithError {
            inventoryService.deletePurchasePrice(id)
        }
    }

    suspend fun deleteResource(id: Long) {
        SafeRequestScope.handleRequestWithError {
            inventoryService.deleteResource(id)
        }
    }

    suspend fun enter(
        dto: InventoryEnterModel, storageItemId: Long, maxLevelFactor: BigDecimal
    ) { // Use the new DTO location
        SafeRequestScope.handleRequestWithError {
            inventoryService.enter(dto.toDTO(storageItemId, maxLevelFactor))
        }
    }

    suspend fun exit(
        dto: InventoryExitModel, storageItemId: Long, isLoss: Boolean = false
    ) { // Use the new DTO location
        SafeRequestScope.handleRequestWithError {
            inventoryService.exit(dto.toDTO(storageItemId, isLoss))
        }
    }

    suspend fun correct(
        dto: InventoryCorrectModel, storageItemId: Long, currentQuantity: BigDecimal
    ) {
        val difference = currentQuantity - dto.amount

        if (difference > BigDecimal.ZERO) {
            // Difference is positive, perform exit (stock out)
            SafeRequestScope.handleRequestWithError {
                inventoryService.exit(
                    dto.toDTO(storageItemId)
                        .copy(countOnUnit = difference, isLoss = dto.isLoss ?: false)
                )
            }
        } else if (difference < BigDecimal.ZERO) {
            // Difference is negative, perform enter (stock in)
            SafeRequestScope.handleRequestWithError {
                inventoryService.enter(
                    dto.toDTO(storageItemId).copy(countOnUnit = -difference, isLoss = false)
                ) // Enter is not a loss, use absolute difference
            }
        } else {
            // Difference is zero, no change needed
            // You might want to log this or handle it in some other way
        }
    }

    suspend fun getRecentOperations(storageItemId: Long): List<InventoryChangeLogModel> {
        return SafeRequestScope.handleRequest {
            inventoryService.recentOperations(storageItemId)
        } ?: emptyList()
    }

}