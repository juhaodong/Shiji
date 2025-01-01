package domain.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raedghazal.kotlinx_datetime_ext.now
import domain.composable.dialog.basic.DialogViewModel
import domain.food.service.FoodLog
import domain.food.service.FoodLogRequest
import domain.food.service.FoodLogService
import domain.food.service.NutritionService
import domain.food.service.RecommendationAndActualResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.SafeRequestScope
import modules.utils.globalDialogManager
import view.page.homePage.NavigationItem

@AppScope
@Inject
class NutritionVM(
    val identityVM: IdentityVM,
    val globalSettingManager: GlobalSettingManager,
    val nutritionService: NutritionService,

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

    var showFoodLogDetailDialog by mutableStateOf(false)
    var selectedFoodLog: FoodLog? by mutableStateOf(null)

    fun showFoodLog(foodLog: FoodLog) {
        showFoodLogDetailDialog = true
        selectedFoodLog = foodLog
    }

    fun showDataAtDate() {
        loading = true
        viewModelScope.launch {
            val deferredData = async {
                SafeRequestScope.handleRequest {
                    nutritionService.getNutritionData(
                        identityVM.currentUser!!.uid,
                        currentDateRange.first,
                        currentDateRange.second
                    )

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
        val imageUrl = identityVM.uploadFile(imageByteArray)
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
                currentDateRange.first,
                currentDateRange.second
            )
        } ?: listOf()
        foodLogList.clear()
        foodLogList.addAll(remote)
        foodLogLoading = false
    }


    fun deleteFoodLog(foodLog: FoodLog) {

        globalDialogManager.confirmAnd(
            "您是否要删除本条记录",
            "你删除了，吃进去的也不会吐出来的哦，不要逃避！！"
        ) {
            foodLogLoading = true
            viewModelScope.launch {
                foodLogService.deleteFoodLog(foodLog.id!!.toLong())
                refreshFoodLog()
                selectedFoodLog = null
                showFoodLogDetailDialog = false
                foodLogLoading = false
            }
        }
    }

    fun confirmDateRange(dateRange: Pair<LocalDate, LocalDate>) {
        showDateDialog = false
        currentDateRange = dateRange
        showDataAtDate()
    }


}