package dataLayer.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.raedghazal.kotlinx_datetime_ext.LocalDateTimeFormatter
import com.raedghazal.kotlinx_datetime_ext.Locale
import com.raedghazal.kotlinx_datetime_ext.now
import com.raedghazal.kotlinx_datetime_ext.plus
import dataLayer.model.Reservation
import dataLayer.model.ReservationReadyDTO
import dataLayer.serivce.ReservationService
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atTime
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import modules.network.AppScope
import modules.network.IKNetworkRequest
import kotlin.time.Duration.Companion.days

@AppScope
@Inject
class ReservationRepository(
    private val reservationService: ReservationService,
) {

    var deviceId by mutableStateOf("1")

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    suspend fun getReservationList(): List<Reservation> {
        val formatter = LocalDateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.default())
        val start = LocalDate.now().atTime(3, 59)
        val end = LocalDate.now().atTime(4, 0).plus(1.days)
        return reservationService.getReservationList(
            deviceId = deviceId,
            fromDateTime = formatter.format(start),
            toDateTime = formatter.format(end)
        ).data
    }


    suspend fun reservationIsReady(): Boolean {
        val res = IKNetworkRequest.handleRequest {
            reservationService.reservationReady()
        }
        if (res != null) {
            try {
                val ready = json.decodeFromString<ReservationReadyDTO>(res)
                return ready.isReady()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    suspend fun cancelReservation(id: Int) {
        reservationService.cancelReservation(id)
    }

    suspend fun checkIn(id: Int) {
        reservationService.checkIn(id)
    }

    suspend fun confirmByMerchant(id: Int) {
        reservationService.confirmByMerchant(id)
    }
}