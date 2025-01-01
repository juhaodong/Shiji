package dataLayer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dataLayer.repository.CurrentOrderInfoRepo
import dataLayer.repository.RestaurantInfoRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import modules.network.AppScope

@AppScope
@Inject
class EBonViewModel(
    val currentOrderInfoRepo: CurrentOrderInfoRepo,
    val restaurantInfoRepository: RestaurantInfoRepository
) : ViewModel() {
    private var lastOrderId = -1

    var lastOrderUUID by mutableStateOf("")
    var loading by mutableStateOf(false)
    var useEBon by mutableStateOf(false)
    var useEmail by mutableStateOf(false)
    var canUseEbon by mutableStateOf(false)

    init {
        viewModelScope.launch {
            canUseEbon = restaurantInfoRepository.ebonIsEnable()
        }
    }

    suspend fun refreshLastOrderPointCode() {
        var counter = 0
        useEmail = false
        if (lastOrderId != -1) {
            loading = true
            while (counter < 15 && lastOrderUUID.isBlank()) {
                try {
                    lastOrderUUID = currentOrderInfoRepo.getUUIDByOrderId(lastOrderId)
                    Napier.e("Should be $lastOrderUUID")
                } catch (e: Exception) {
                    Napier.e(e.stackTraceToString())
                }
                delay(1000)
                counter++
            }
            Napier.e("Load")

            loading = false
        }
    }

    suspend fun alsoSendToEmail(email: String) {
        currentOrderInfoRepo.sendInvoiceToEmail(lastOrderUUID, email)
        useEmail = true
    }

    fun startUseEBon(orderId: Int) {
        if (!canUseEbon) {
            return
        }
        useEBon = true
        lastOrderId = orderId
        Napier.e("Should be $orderId")
    }


    fun qrCodeReady(): Boolean {
        return useEBon && lastOrderId != -1 && qrCodeData().isNotBlank()
    }

    fun qrCodeData(): String {
        return if (lastOrderUUID.isNotBlank()) {
            "https://baobao.aaden.io/?uuid=$lastOrderUUID"
        } else {
            ""
        }
    }
}