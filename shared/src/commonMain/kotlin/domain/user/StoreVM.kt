package domain.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raedghazal.kotlinx_datetime_ext.now
import domain.dashboard.DashboardReportRepository
import domain.dashboard.DashboardReportService
import domain.dashboard.TableInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.IKNetworkRequest
import modules.utils.closingToday
import modules.utils.getEndpointUrl
import view.page.homePage.NavigationItem
import view.page.homePage.dataCenterPage.storeDetail.dashboard.DashboardCard

@AppScope
@Inject
class StoreVM(
    val identityVM: IdentityVM,
    val globalSettingManager: GlobalSettingManager,
    val dashboardReportRepository: DashboardReportRepository,
    val dashboardReportService: DashboardReportService
) : ViewModel() {
    val dashboardData = mutableStateListOf<DashboardCard>()
    val tableList = mutableStateListOf<TableInfo>()
    var loading by mutableStateOf(false)
    var selectedNavigationItem by
    mutableStateOf(
        NavigationItem.DataCenter
    )


    private fun showDataAtDate(date: LocalDate) {
        loading = true
        viewModelScope.launch {
            val deferredData = async {
                dashboardReportRepository.getDashboardCardsForDate(
                    identityVM.currentUser?.uid ?: "-", globalSettingManager.selectedDeviceId, date
                )
            }
            val deferredTable =
                async { getRealTimeTableData(globalSettingManager.selectedDeviceId) }

            val data = deferredData.await()
            deferredTable.await()
            dashboardData.clear()
            dashboardData.addAll(data)
            loading = false
        }

    }

    private suspend fun getRealTimeTableData(deviceId: String) {
        val t = IKNetworkRequest.handleRequest {
            dashboardReportService.getRealTimeTableStatus(getEndpointUrl(deviceId))
        } ?: listOf()
        tableList.clear()
        tableList.addAll(t)
    }

    fun showDataForIndex(index: Int) {
        val date = if (index == 0) closingToday() else closingToday().minus(DatePeriod(days = 1))
        showDataAtDate(date)
    }


}