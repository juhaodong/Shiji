package domain.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raedghazal.kotlinx_datetime_ext.now
import domain.composable.dialog.basic.DialogViewModel
import domain.dashboard.DashboardReportRepository
import domain.dashboard.DashboardReportService
import domain.dashboard.TableInfo
import domain.food.user.FoodLog
import domain.food.user.FoodLogRequest
import domain.food.user.FoodLogService
import domain.food.user.NutritionService
import domain.food.user.RecommendationAndActualResponse
import domain.inventory.InventoryRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.auth.currentUser
import modules.network.AppScope
import modules.network.IKNetworkRequest
import modules.network.SafeRequestScope
import modules.utils.closingToday
import modules.utils.getEndpointUrl
import modules.utils.globalDialogManager
import view.page.homePage.NavigationItem
import view.page.homePage.dataCenterPage.storeDetail.dashboard.DashboardCard

@AppScope
@Inject
class NutritionVM(
    val identityVM: IdentityVM,
    val globalSettingManager: GlobalSettingManager,
    val nutritionService: NutritionService,
    val inventoryRepository: InventoryRepository,
    val foodLogService: FoodLogService,
    val dialogViewModel: DialogViewModel
) : ViewModel() {


    var currentDateRange by mutableStateOf(LocalDate.now() to LocalDate.now())
    var showDateDialog by mutableStateOf(false)

    var loading by mutableStateOf(false)
    var info: RecommendationAndActualResponse? by mutableStateOf(null)
    var selectedNavigationItem by mutableStateOf(
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
            refreshFoodLog()

            info = deferredData.await()

            loading = false
        }

    }

    var foodLogLoading by mutableStateOf(false)
    suspend fun createFoodLog(personCount: Int, imageByteArray: ByteArray) {
        foodLogLoading = true
        val imageUrl = inventoryRepository.uploadFile(imageByteArray)
        if (imageUrl != null) {
            try {
                foodLogService.saveFoodLog(
                    FoodLogRequest(
                        imageUrl = imageUrl,
                        uid = identityVM.currentUser!!.uid,
                        personCount = personCount.toString()
                    )
                )
                refreshFoodLog()
            } catch (e: Exception) {
                globalDialogManager.confirmAnd("请求失败", e.message ?: "原因未知")
            }

        }
        foodLogLoading = false
    }

    fun lastLog(): FoodLog? {
        return foodLogList.firstOrNull()
    }

    val foodLogList = mutableStateListOf<FoodLog>()
    suspend fun refreshFoodLog() {
        foodLogLoading = false
        val remote = SafeRequestScope.handleRequest {
            foodLogService.findFoodLogsByUidAndDateRange(
                identityVM.currentUser!!.uid,
                LocalDate.now(),
                LocalDate.now()
            )
        } ?: listOf()
        foodLogList.clear()
        foodLogList.addAll(remote)
        foodLogLoading = false
    }


    fun showDataForIndex(index: Int) {
        val date = if (index == 0) closingToday() else closingToday().minus(DatePeriod(days = 1))
        showDataAtDate(date)
    }


}