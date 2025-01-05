package domain.inventory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.form.OptionFormField
import domain.composable.dialog.form.StorageAmountFormField
import domain.composable.dialog.form.TextFormField
import domain.composable.dialog.form.UnitEditorFormField
import domain.composable.dialog.selection.SelectOption
import domain.inventory.model.DashboardDataDTO
import domain.inventory.model.InventorySetting
import domain.inventory.model.InventorySettingDTO
import domain.inventory.model.StorageOperationType
import domain.inventory.model.change.InventoryChangeLogModel
import domain.inventory.model.change.InventoryCorrectModel
import domain.inventory.model.change.InventoryEnterModel
import domain.inventory.model.change.InventoryExitModel
import domain.inventory.model.storageItem.ResourceCategoryModel
import domain.inventory.model.storageItem.ShopResourceEditModel
import domain.inventory.model.storageItem.StorageItemModel
import domain.inventory.model.storageItem.UnitDTO
import domain.inventory.model.storageItem.toDTO
import domain.user.IdentityVM
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.SafeRequestScope
import modules.utils.globalDialogManager
import view.page.homePage.inventoryPage.resource.StorageItemAction

@AppScope
class InventoryViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    val identityVM: IdentityVM,
    val dialogViewModel: DialogViewModel,
    private val globalSettingManager: GlobalSettingManager,
    val inventoryService: InventoryService,
) : ViewModel() {
    var currentInventorySetting: InventorySetting? by mutableStateOf(null)
    var subscriptionStatus: Boolean by mutableStateOf(false)
    var storageOperationType: StorageOperationType? by mutableStateOf(null)
    var activateLoading: Boolean by mutableStateOf(true)
    fun refreshInventoryStatus() {
        viewModelScope.launch {
            if (currentInventorySetting == null || currentInventorySetting?.shopId != globalSettingManager.selectedDeviceId.toLong()) {
                activateLoading = true
                val setting =
                    inventoryRepository.getInventorySetting(globalSettingManager.selectedDeviceId)
                currentInventorySetting = setting
                subscriptionStatus =
                    inventoryRepository.getInventorySubscriptionStatus(globalSettingManager.selectedDeviceId)
                activateLoading = false
            }


        }
    }


    var dashboardData: DashboardDataDTO? by mutableStateOf(null)
    var dashboardLoading: Boolean by mutableStateOf(false)
    fun loadDashboardData() {
        viewModelScope.launch {
            dashboardLoading = true
            dashboardData =
                inventoryRepository.getDashboardInfo(globalSettingManager.selectedDeviceId.toLong())
            dashboardLoading = false
        }
    }


    fun saveSetting() {
        viewModelScope.launch {
            try {
                activateLoading = true
                refreshInventoryStatus()
            } catch (e: Exception) {
                globalDialogManager.confirmAnd(
                    title = "激活失败", content = e.message ?: "不明原因"
                )

            }
        }
    }


    val resourceCategoryList = mutableStateListOf<ResourceCategoryModel>()
    var resourceCategoryLoading: Boolean by mutableStateOf(false)
    var selectedCategoryId: Long? by mutableStateOf(null)


    fun activeCategoryIndex(): Int {
        return if (selectedCategoryId == null) {
            0
        } else {
            resourceCategoryList.indexOfFirst { it.id == selectedCategoryId } + 1
        }
    }

    fun changeSelectedCategoryId(id: Long?) {
        selectedCategoryId = id
        loadStorageItemList()
    }

    private suspend fun getResourceCategoryList(): List<ResourceCategoryModel> {
        return inventoryRepository.getResourceCategoryList()
    }

    fun loadResourceCategories() {
        viewModelScope.launch {
            resourceCategoryLoading = true
            selectedCategoryId = null
            try {
                resourceCategoryList.clear()
                resourceCategoryList.addAll(
                    getResourceCategoryList()

                )
            } catch (e: Exception) {
                // Handle error, e.g., show error message
            } finally {
                resourceCategoryLoading = false
            }
        }
    }

    suspend fun saveResourceCategory(name: String, id: Long? = null): ResourceCategoryModel? {
        try {
            val new = inventoryRepository.saveResourceCategory(
                ResourceCategoryModel(
                    name, shopId = globalSettingManager.selectedDeviceId.toLong(), id = id
                )
            )
            loadResourceCategories()
            return new
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    var categoryManageDialog: Boolean by mutableStateOf(false)
    fun deleteResourceCategory(id: Long) {
        viewModelScope.launch {
            try {
                inventoryRepository.deleteResourceCategory(id)
                // Optionally reload categories after delete
                loadResourceCategories()
            } catch (e: Exception) {
                // Handle error, e.g., show error message
            }
        }
    }

    var storageItemList: List<StorageItemModel> by mutableStateOf(emptyList())
    var storageItemLoading: Boolean by mutableStateOf(false)
    fun loadStorageItemList() {
        viewModelScope.launch {
            storageItemLoading = true
            storageItemList =
                inventoryRepository.getStorageItemList(activeCategoryId = selectedCategoryId)
            storageItemLoading = false
        }
    }

    var searching by mutableStateOf(false)
    var searchText by mutableStateOf("")

    fun filteredItemList(): List<StorageItemModel> {
        return storageItemList.filter {
            !searching || searchText.isBlank() || it.name.contains(searchText)
        }.sortedBy { it.storageItemCategory.id }
    }

    fun startSearch() {
        changeSelectedCategoryId(null)
        searchText = ""
        searching = true
    }


    fun deleteStorageItem(id: Long) {
        viewModelScope.launch {
            inventoryRepository.deleteResource(id)
            loadStorageItemList()
        }
    }

    fun editStorageItem(targetModel: StorageItemModel? = null) {
        viewModelScope.launch {
            val model: ShopResourceEditModel = dialogViewModel.showFormDialog(
                TextFormField(
                    keyName = "name", label = "名称", defaultValue = targetModel?.name
                ),
                dialogViewModel.createAsyncOptionFormField(
                    keyName = "parentId",
                    label = "分类名称",
                    addNewOption = {
                        val name = dialogViewModel.showInput("请输入分类名称")
                        saveResourceCategory(name)!!.let {
                            SelectOption(value = it.id!!, label = it.name)
                        }
                    },
                    loadOptions = {
                        getResourceCategoryList().map {
                            SelectOption(
                                value = it.id!!, label = it.name
                            )
                        }
                    },
                    defaultValue = targetModel?.storageItemCategory?.id ?: selectedCategoryId
                ),

                UnitEditorFormField("unitList", targetModel?.units?.map {
                    UnitDTO(
                        it.name, it.nextLevelFactor
                    )
                }),
                TextFormField(keyName = "maxUnitPrice",
                    label = "最大单位价格",
                    keyboardType = KeyboardType.Decimal,
                    defaultValue = targetModel?.let { (it.unitPrice * it.maxLevelFactor).toPlainString() }),
                TextFormField(
                    keyName = "sku",
                    label = "Sku",
                    required = false,
                    placeHolder = "如果不填写，则会随机生成",
                    defaultValue = targetModel?.primarySku
                ),
                TextFormField(
                    keyName = "inventoryPeriodDays",
                    label = "库存统计周期",
                    keyboardType = KeyboardType.Number,
                    defaultValue = targetModel?.inventoryPeriodDays?.toString() ?: "7"
                ),
                title = if (targetModel != null) "编辑原料:" + targetModel.name else "新建原料"
            )
            inventoryRepository.saveResource(
                model.toDTO(
                    shopId = globalSettingManager.selectedDeviceId.toLong(), id = targetModel?.id
                )
            )
            loadStorageItemList()


        }
    }

    fun enter(model: StorageItemModel) {
        viewModelScope.launch {
            val editModel: InventoryEnterModel = dialogViewModel.showFormDialog(
                StorageAmountFormField("amount", model.units),
                TextFormField(
                    "overridePrice",
                    "入库价格（最大单位价格）",
                    defaultValue = model.maxUnitPrice().toPlainString()
                ),
                TextFormField.getNoteField(),
                title = StorageItemAction.StockIn.label,
                subtitle = "正在操作: " + model.name
            )
            inventoryRepository.enter(editModel, model.id, model.maxLevelFactor)
            loadStorageItemList()
        }
    }

    fun exit(model: StorageItemModel, isLoss: Boolean) {
        viewModelScope.launch {
            val editModel: InventoryExitModel = dialogViewModel.showFormDialog(
                StorageAmountFormField("amount", model.units),
                TextFormField.getNoteField(required = isLoss),
                title = if (isLoss) StorageItemAction.Loss.label else StorageItemAction.StockOut.label,
                subtitle = "正在操作: " + model.name
            )
            inventoryRepository.exit(editModel, model.id, isLoss)
            loadStorageItemList()
            // Handle success or error, e.g., update UI state
        }
    }

    fun change(model: StorageItemModel) {
        viewModelScope.launch {
            val editModel: InventoryCorrectModel = dialogViewModel.showFormDialog(
                StorageAmountFormField("amount", model.units),
                OptionFormField(
                    keyName = "isLoss",
                    label = "是否报损",
                    options = listOf(SelectOption("是", true), SelectOption("否", false)),
                    required = false
                ),
                TextFormField.getNoteField(),
                title = StorageItemAction.Adjust.label,
                subtitle = "正在操作: " + model.name
            )
            inventoryRepository.correct(editModel, model.id, model.currentCount)
            loadStorageItemList()
            // Handle success or error, e.g., update UI state
        }
    }

    var recentChanges = mutableStateListOf<InventoryChangeLogModel>()
    var recentChangesLoading by mutableStateOf(false)
    var recentChangesDialog by mutableStateOf(false)
    var recentChangeTitle by mutableStateOf("")

    fun showRecentChangesDialog(storageItemId: Long?) {
        viewModelScope.launch {
            recentChangeTitle = storageItemList.find { it.id == storageItemId }?.name ?: ""
            recentChangesDialog = true
            recentChangesLoading = true
            recentChanges.clear()
            if (storageItemId == null) {
                recentChanges.addAll(SafeRequestScope.handleRequest {
                    inventoryService.recentOperationsByShop(
                        globalSettingManager.selectedDeviceId.toLong()
                    )
                } ?: listOf())
            } else {
                recentChanges.addAll(inventoryRepository.getRecentOperations(storageItemId))
            }
            recentChangesLoading = false
        }
    }

    var showResourceDetailDialog by mutableStateOf(false)
    var resourceDetail: StorageItemDetailDTO? by mutableStateOf(null)
    var resourceDetailLoading by mutableStateOf(false)
    fun showResourceDetail(id: Long) {
        showResourceDetailDialog = true
        viewModelScope.launch {
            resourceDetailLoading = true
            resourceDetail = SafeRequestScope.handleRequest {
                inventoryService.storageDetail(id)
            }
            resourceDetailLoading = false


        }

    }


    init {
        refreshInventoryStatus()
    }

}