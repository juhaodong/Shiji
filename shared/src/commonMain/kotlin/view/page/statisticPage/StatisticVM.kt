package view.page.statisticPage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.raedghazal.kotlinx_datetime_ext.now
import domain.dashboard.DashboardReportRepository
import domain.dashboard.DishStatisticModel
import domain.dashboard.HourlyReportModel
import domain.dashboard.PaymentInfo
import domain.dashboard.ServantPayment
import domain.dashboard.TaxInfoModel
import domain.user.IdentityVM
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.utils.FormatUtils.sumOfB
import view.page.homePage.dataCenterPage.storeDetail.dashboard.DashboardCard


@AppScope
@Inject
class StatisticVM(
    val identityVM: IdentityVM,
    val globalSettingManager: GlobalSettingManager,
    val dashboardReportRepository: DashboardReportRepository
) : ViewModel() {
    val dashboardData = mutableStateListOf<DashboardCard>()
    val dishStatistics = mutableStateListOf<DishStatisticModel>()
    val taxInfoList = mutableStateListOf<TaxInfoModel>()
    val hourlyReports = mutableStateListOf<HourlyReportModel>() // Add this line
    val paymentInfoList = mutableStateListOf<PaymentInfo>()
    val servantInfoList = mutableStateListOf<ServantPayment>()

    var currentDateRange by mutableStateOf(LocalDate.now() to LocalDate.now())
    var showDateDialog by mutableStateOf(false)
    var loading by mutableStateOf(false)


    fun showDataAtDate() {
        loading = true

        viewModelScope.launch {
            dashboardData.clear()
            val data = dashboardReportRepository.getCombinedDashboardData(
                identityVM.currentUser?.uid ?: "-",
                globalSettingManager.selectedDeviceId,
                currentDateRange
            )
            dishStatistics.clear()
            dashboardData.clear()
            taxInfoList.clear()
            paymentInfoList.clear()
            servantInfoList.clear()
            hourlyReports.clear() // Clear existing data
            if (data != null) {
                dishStatistics.addAll(data.dishStatistics)
                dashboardData.addAll(data.dashboardCards)
                taxInfoList.addAll(data.taxInfo)
                paymentInfoList.addAll(data.paymentInfo)
                servantInfoList.addAll(data.servantInfo)
                hourlyReports.addAll(data.hourlyReports)
            } else {
                if (identityVM.currentUser != null && globalSettingManager.selectedDeviceId.isNotBlank()) {
                    identityVM.currentlyOffline = true
                }
            }
            loading = false
        }

    }

    fun totalIncome(): BigDecimal {
        return taxInfoList.filter { it.taxName == "Total" }.sumOfB { it.total }
    }

    fun inHouseTotalIncome(): BigDecimal {
        return taxInfoList.filter { it.taxName == "In Haus" }.sumOfB { it.total }
    }

    fun takeawayTotalIncome(): BigDecimal {
        return taxInfoList.filter { it.taxName == "Außer Haus" }.sumOfB { it.total }
    }


    fun confirmDateRange(dateRange: Pair<LocalDate, LocalDate>) {
        showDateDialog = false
        currentDateRange = dateRange
        showDataAtDate()
    }


}