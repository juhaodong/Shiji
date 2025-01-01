package dataLayer.serivce

import dataLayer.model.Reservation

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import modules.network.IKResponse
import modules.network.NormalResponse

const val host = "https://reservation-api.aaden.io/reservation/"

interface ReservationService {
    @GET(host + "getList/{deviceId}")
    suspend fun getReservationList(
        @Path("deviceId") deviceId: String,
        @Query("fromDateTime") fromDateTime: String,
        @Query("toDateTime")
        toDateTime: String,
    ): NormalResponse<List<Reservation>>

    @POST(host + "cancelInternal/{id}")
    suspend fun cancelReservation(
        @Path("id") id: Int,
    )

    @POST(host + "checkIn/{id}")
    suspend fun checkIn(
        @Path("id") id: Int,
    )

    @POST(host + "confirmByMerchant/{id}")
    suspend fun confirmByMerchant(
        @Path("id") id: Int,
    )

    @POST("Tables.php?op=checkUserAvailable")
    suspend fun reservationReady(
    ): IKResponse<String?>



}

