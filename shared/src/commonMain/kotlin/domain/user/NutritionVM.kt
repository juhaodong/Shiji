package domain.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.dashboard.DashboardReportRepository
import domain.dashboard.DashboardReportService
import domain.dashboard.TableInfo
import domain.food.user.NutritionService
import domain.food.user.RecommendationAndActualResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.IKNetworkRequest
import modules.network.SafeRequestScope
import modules.utils.closingToday
import modules.utils.getEndpointUrl
import view.page.homePage.NavigationItem
import view.page.homePage.dataCenterPage.storeDetail.dashboard.DashboardCard

@AppScope
@Inject
class NutritionVM(
    val identityVM: IdentityVM,
    val globalSettingManager: GlobalSettingManager,
    val nutritionService: NutritionService
) : ViewModel() {

    var loading by mutableStateOf(false)
    var info: RecommendationAndActualResponse? by mutableStateOf(null)
    var selectedNavigationItem by
    mutableStateOf(
        NavigationItem.DataCenter
    )


    private fun showDataAtDate(date: LocalDate) {
        loading = true
        viewModelScope.launch {
            val deferredData = async {
                SafeRequestScope.handleRequest {
                    nutritionService.getNutritionData(identityVM.currentUser!!.uid, date, date)

                }
            }

            info = deferredData.await()

            loading = false
        }

    }


    fun showDataForIndex(index: Int) {
        val date = if (index == 0) closingToday() else closingToday().minus(DatePeriod(days = 1))
        showDataAtDate(date)
    }


}