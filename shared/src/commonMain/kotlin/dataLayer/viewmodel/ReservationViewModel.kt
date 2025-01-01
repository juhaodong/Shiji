package dataLayer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dataLayer.model.Reservation
import dataLayer.repository.ReservationRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import modules.network.AppScope

@AppScope
@Inject
class ReservationViewModel(
    private val reservationRepository: ReservationRepository,
) : ViewModel() {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }
    val reservations = mutableStateListOf<Reservation>()

    var loading by mutableStateOf(false)
    var ready by mutableStateOf(false)
    var showConfirmCancelDialog by mutableStateOf(false)
    var selectedReservation: Reservation? by mutableStateOf(null)
    var showReservationDetail by mutableStateOf(false)
    var reservationId: Int? by mutableStateOf(null)

    var showScanQRDialog by mutableStateOf(false)

    init {
        viewModelScope.launch {
            ready = reservationRepository.reservationIsReady()
            loadAnd { }

        }

    }

    @Serializable
    class QRCode(val id: Int? = null)

    fun decodeQR(qr: String) {
        val id = try {
            json.decodeFromString<QRCode>(qr.replace("'", "")).id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        reservationId = id
        showReservationWithId(id)
    }

    fun showReservationWithId(id: Int?) {
        selectedReservation = reservations.find { it.id == id }
        showReservationDetail = true

    }

    private suspend fun loadReservations() {
        val response = reservationRepository.getReservationList()
        reservations.clear()
        reservations.addAll(response)
    }

    fun loadAnd(action: suspend () -> Unit) {
        if (!ready) {
            return
        }
        viewModelScope.launch {
            loading = true
            action()
            loadReservations()
            selectedReservation = null
            showReservationDetail = false
            loading = false
        }
    }


    fun cancelReservation(id: Int) {
        loadAnd {
            reservationRepository.cancelReservation(id)
        }
    }

    fun checkIn(id: Int) {
        loadAnd {
            reservationRepository.checkIn(id)
        }
    }

    fun confirmByMerchant(id: Int) {
        loadAnd {
            reservationRepository.confirmByMerchant(id)
        }
    }
}