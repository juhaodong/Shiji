package domain.dashboard

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.serialization.Serializable
import modules.network.IKResponse
import modules.network.cloudUrl
import modules.utils.BigDecimalSerializer
import view.page.homePage.dataCenterPage.storeDetail.dashboard.DashboardCard


private const val BASE_URL = "$cloudUrl/dashboard-report" // Or your actual base URL

interface DashboardReportService {
    @GET("$BASE_URL/{userId}/cards")
    suspend fun getUserDashboardCards(
        @Path("userId") userId: String,
        @Query("deviceId") deviceId: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<DashboardCard>

    @GET("$BASE_URL/{userId}/combinedData")
    suspend fun getUserCombinedDashboardData(
        @Path("userId") userId: String,
        @Query("deviceId") deviceId: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): CombinedDashboardData

    @GET("{ngrokUrl}Tables.php?op=showAllTableWithCells")
    suspend fun getRealTimeTableStatus(@Path("ngrokUrl") ngrokUrl: String): IKResponse<List<TableInfo>>
}


@Serializable
data class TableInfo(
    @Serializable(with = BigDecimalSerializer::class)
    val totalPrice: BigDecimal? = BigDecimal.ZERO,
    val tableName: String,
    val usageStatus: Int,
    val tableId: Int,
    val sectionId: Int
)

@Serializable
data class CombinedDashboardData(
    val dashboardCards: List<DashboardCard>,
    val dishStatistics: List<DishStatisticModel>,
    val taxInfo: List<TaxInfoModel>,
    val paymentInfo: List<PaymentInfo>,
    val servantInfo: List<ServantPayment>,
    val hourlyReports: List<HourlyReportModel>
)

