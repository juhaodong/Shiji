package dataLayer.repository

import dataLayer.model.order.ConsumeTypeModel
import dataLayer.model.payment.PayMethodModel
import dataLayer.model.restaurant.FreeInformationModel
import dataLayer.model.restaurant.RestaurantInfoEntity
import dataLayer.model.restaurant.SimpleTableModel
import dataLayer.model.restaurant.TableModel
import dataLayer.serivce.RestaurantInfoService
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.IKNetworkRequest

@Serializable
data class SettingInfo(
    val section: String,
    val sKey: String,
    val sValue: String,
    val defaultValue: String,
    val sType: String,
    val minimumVersion: String,
    val sOptions: String,
    val tagList: String,
)

@AppScope
@Inject
class RestaurantInfoRepository(
    private val restaurantInfoService: RestaurantInfoService,
    private val globalSettingManager: GlobalSettingManager,
) {
    private var consumeTypeList: List<ConsumeTypeModel>? = null



    suspend fun getCurrentConsumeTypeList(): List<ConsumeTypeModel> {
        return IKNetworkRequest.handleRequest {
            restaurantInfoService.getCurrentConsumeType(
                globalSettingManager.lang
            )
        } ?: listOf()
    }

    suspend fun getConsumeTypeById(consumeTypeId: Int): ConsumeTypeModel? {
        if (consumeTypeList == null) {
            consumeTypeList = getCurrentConsumeTypeList()
        }
        return consumeTypeList!!.find { it.id == consumeTypeId }
    }


    suspend fun getRestaurantInfo(): RestaurantInfoEntity? {
        return IKNetworkRequest.handleRequest { restaurantInfoService.getRestaurantInfo() }
            ?.getOrNull(0)
    }

    suspend fun checkBoss(pw: String): Boolean {
        return IKNetworkRequest.handleRequest { restaurantInfoService.checkBoss(pw) } != null
    }

    suspend fun checkServant(pw: String): Boolean {
        return IKNetworkRequest.handleRequest { restaurantInfoService.checkServant(pw) } != null
    }


    suspend fun getPaymentList(): List<PayMethodModel> {
        return IKNetworkRequest.handleRequest {
            restaurantInfoService.getPaymentMethod(globalSettingManager.lang)
        } ?: listOf()
    }

    suspend fun getFreeInformationList(): List<FreeInformationModel> {
        return IKNetworkRequest.handleRequest {
            restaurantInfoService.showAllFreeInformation(
                globalSettingManager.lang
            )
        } ?: listOf()
    }

    suspend fun sendFreeInformation(tableId: Int, freeInformationId: Int) {
        IKNetworkRequest.handleRequest {
            restaurantInfoService.sendFreeInformation(
                tableId = tableId,
                freeInformationId = freeInformationId
            )
        }
    }

    suspend fun getTableList(): List<TableModel> {
        return IKNetworkRequest.handleRequest { restaurantInfoService.showTableList() } ?: listOf()
    }

    suspend fun getTable(tableId: Int): SimpleTableModel? {
        return IKNetworkRequest.handleRequest { restaurantInfoService.getTableStateById(tableId = tableId) }
            ?.getOrNull(0)
    }

    suspend fun ebonIsEnable(): Boolean {
        return IKNetworkRequest.handleRequest { restaurantInfoService.getEBonEnable() }?.enabled
            ?: false
    }

    suspend fun currentDeviceId(): String {
        return IKNetworkRequest.handleRequestWithError { restaurantInfoService.getDeviceId() }
    }


}