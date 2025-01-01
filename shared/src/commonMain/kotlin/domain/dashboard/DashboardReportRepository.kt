package domain.dashboard

import kotlinx.datetime.LocalDate
import me.tatarka.inject.annotations.Inject
import modules.network.AppScope
import view.page.homePage.dataCenterPage.storeDetail.dashboard.DashboardCard

@Inject
@AppScope
class DashboardReportRepository(private val service: DashboardReportService) {
    suspend fun getDashboardCardsForDate(
        userId: String,
        deviceId: String,
        date: LocalDate
    ): List<DashboardCard> {
        return getDashboardCardsForDateRange(userId, deviceId, date to date)
    }


    suspend fun getCombinedDashboardData(
        userId: String,
        deviceId: String,
        dateRange: Pair<LocalDate, LocalDate>
    ): CombinedDashboardData? {
        return try {
            service.getUserCombinedDashboardData(
                userId,
                deviceId,
                dateRange.first.toString(),
                dateRange.second.toString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun getDashboardCardsForDateRange(
        userId: String,
        deviceId: String,
        dateRange: Pair<LocalDate, LocalDate>
    ): List<DashboardCard> {
        return try {
            service.getUserDashboardCards(
                userId,
                deviceId,
                dateRange.first.toString(),
                dateRange.second.toString()
            )
        } catch (e: Exception) {
            emptyList()
        }
    }
}