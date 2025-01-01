package domain.supplier

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.composable.dialog.basic.DialogViewModel
import domain.supplier.model.SupplierSetting
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.SafeRequestScope
import modules.utils.globalDialogManager

@Inject
@AppScope
class SupplierViewModel(
    val supplierRepository: SupplierRepository,
    val dialogViewModel: DialogViewModel,
    val globalSettingManager: GlobalSettingManager,
    val supplierService: SupplierService
) : ViewModel() {
    val suppliers = mutableStateListOf<SupplierSetting>()
    var searchText by mutableStateOf("")
    var searching by mutableStateOf(false)

    fun filteredSupplier(): List<SupplierSetting> {
        return suppliers.filter {
            (!searching || searchText.isBlank()) || it.name.contains(searchText)
        }
    }


    var supplierLoading by mutableStateOf(false)

    fun loadSuppliers() {
        viewModelScope.launch {
            supplierLoading = true
            suppliers.clear()
            suppliers.addAll(supplierRepository.getSuppliers(searchText))
            supplierLoading = false
        }
    }

    suspend fun bindSupplier(supplierId: Long) {
        val shopId = globalSettingManager.selectedDeviceId
        SafeRequestScope.handleRequest {
            supplierService.bindSupplier(shopId = shopId, supplierId = supplierId)
            globalDialogManager.confirmAnd("绑定成功！", "正在为您导入供应商默认商品...请稍侯")
            delay(1000)
            globalDialogManager.confirmDialog = false
        }
        loadSuppliers()

    }


}