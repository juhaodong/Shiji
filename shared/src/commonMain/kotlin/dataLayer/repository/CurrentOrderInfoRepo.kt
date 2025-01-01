@file:OptIn(ExperimentalCoroutinesApi::class)

package dataLayer.repository


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dataLayer.model.SendDTO
import dataLayer.model.order.LocalOrderInfo
import dataLayer.model.order.NetworkDown
import dataLayer.model.order.NoOrder
import dataLayer.model.order.OrderInfoModel
import dataLayer.model.order.newTakeawayOrderInfo
import dataLayer.model.order.notOpenOrderInfo
import dataLayer.serivce.OrderService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import modules.network.AppScope
import modules.network.IKNetworkRequest

@AppScope
@Inject
class CurrentOrderInfoRepo(
    private val orderService: OrderService,
    val restaurantInfoRepository: RestaurantInfoRepository,
) {
    var currentTableId by mutableStateOf(-1)
        private set


    suspend fun getCurrentOrderInfo(): LocalOrderInfo {
        return if (currentTableId != -1) {
            val order = getOrderInfo(currentTableId)
            if (order == null) {
                if (currentTableId != -1) {
                    val realTable = restaurantInfoRepository.getTable(currentTableId)
                    if (realTable != null) {
                        return notOpenOrderInfo(realTable.id, realTable.name)
                    } else if (currentTableId == -2) {
                        return newTakeawayOrderInfo()
                    }
                }
                NetworkDown
            } else {
                order
            }
        } else {
            NoOrder
        }

    }


    fun setWhoAmI(tableId: Int) {
        currentTableId = tableId
    }


    suspend fun getOrderInfo(tableId: Int): LocalOrderInfo? {
        return IKNetworkRequest.handleRequest(reportError = false) {
            orderService.getTableInfo(
                tableId,
            )
        }?.toOrderInfo()
    }

    suspend fun getUUIDByOrderId(orderId: Int): String {
        return getOrderInfoById(orderId)?.electronicUuid
            ?: ""
    }

    suspend fun sendInvoiceToEmail(uuid: String, email: String) {
        try {
            Napier.e(">>>Send Invoice To Email:" + email)
            orderService.sendInvoiceEmail(
                Json.encodeToString(
                    SendDTO(
                        uuid = uuid,
                        mailTo = email,
                        templateId = "z3m5jgr8xpoldpyo"
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private suspend fun getOrderInfoById(orderId: Int): OrderInfoModel? {
        return IKNetworkRequest.handleRequest {
            orderService.getOrderInfoByOrderId(
                orderId,
                chaos = Clock.System.now().nanosecondsOfSecond.toString()
            )
        }
            ?.getOrNull(0)
    }

}