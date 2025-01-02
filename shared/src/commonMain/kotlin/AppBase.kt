import shijiapp.shared.generated.resources.Res
import shijiapp.shared.generated.resources._ChangeTheme
import shijiapp.shared.generated.resources._Close
import shijiapp.shared.generated.resources._DarkMode
import shijiapp.shared.generated.resources._SystemSetting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.materialkolor.PaletteStyle
import domain.composable.basic.layout.SmallSpacer
import domain.composable.dialog.ComingSoonDialog
import domain.composable.dialog.ConfirmDialog
import domain.composable.dialog.DateInputDialog
import domain.composable.dialog.LoadingDialog
import domain.composable.dialog.OfflineDialog
import domain.composable.dialog.SuccessDialog
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.changeLanguageDialog.ChangeLanguageDialog
import domain.composable.dialog.form.BaseFormDialog
import domain.composable.dialog.inputDialog.InputDialog
import domain.composable.dialog.selection.SimpleSelectionDialog
import domain.inventory.InventoryViewModel
import domain.inventory.model.OrderStatus
import domain.purchaseOrder.PurchaseOrderVM
import domain.supplier.OrderBookViewModel
import domain.supplier.SupplierViewModel
import domain.user.AddStoreDialog
import domain.user.IdentityVM
import domain.user.StoreVM
import domain.user.model.UserStoreAuth
import io.github.skeptick.libres.LibresSettings
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.startRoute
import modules.physic.changeLocale
import modules.utils.displayCurrency
import modules.utils.getNgrokUrl
import modules.utils.globalDialogManager
import org.jetbrains.compose.resources.stringResource
import theme.AadenMenuTheme
import theme.CurrentTheme
import theme.colorsSets
import view.StoreManagementDialog
import view.page.TeamManagePage
import view.page.activatePage.ActivatePage
import view.page.homePage.NavigationItem
import view.page.homePage.dataCenterPage.DataCenterPage
import view.page.homePage.inventoryPage.dashboard.InventoryDashboard
import view.page.homePage.inventoryPage.resource.StorageItemListPage
import view.page.homePage.supplierManagePage.orderBook.OrderBookDetailPage
import view.page.homePage.supplierManagePage.orderBook.OrderMenuPage
import view.page.homePage.supplierManagePage.orderBook.ProductDetailPage
import view.page.homePage.supplierManagePage.orderBook.ProductImportPage
import view.page.homePage.supplierManagePage.orderBook.ProductListPage
import view.page.homePage.supplierManagePage.orders.OrderConfirmPage
import view.page.homePage.supplierManagePage.orders.OrderDetailsPage
import view.page.homePage.supplierManagePage.orders.OrderListPage
import view.page.homePage.supplierManagePage.orders.OrderSignPage
import view.page.homePage.supplierManagePage.orders.OrderSuccessPage
import view.page.homePage.supplierManagePage.supplierlistpage.SupplierListPage
import view.page.homePage.supplierManagePage.supplierlistpage.findSupplierPage.FindSupplierPage
import view.page.homePage.workbenchPage.WorkbenchPage
import view.page.loginPage.LoginPage
import view.page.statisticPage.StatisticCenterPage
import view.page.statisticPage.StatisticVM

object RouteName {
    const val LOGIN = "login"
    const val ACTIVATE = "activate"
    const val HOME = "home"
    const val LOADING = "loading"
    const val STATISTIC_CENTER = "statisticCenter"
    const val INVENTORY_LIST = "inventoryList"

    object Supplier {
        const val FIND_SUPPLIER = "findSupplier"

        const val ORDER_BOOK_DETAIL = "OrderBookDetail"
        const val ORDER_MENU = "OrderProduct"

        const val PRODUCT_IMPORT = "ProductImport"
        const val PRODUCT_LIST = "ProductList"

        const val PRODUCT_DETAIL = "ProductEdit"

        const val ORDER_CONFIRM = "OrderConfirm"
        const val ORDER_SUCCESS = "OrderSuccess"
        const val ORDER_LIST = "OrderList"
        const val ORDER_DETAILS = "OrderDetails"
        const val ORDER_SIGN = "OrderSign"
    }

    const val TeamManage = "TeamManage"
}


typealias AppBase = @Composable () -> Unit


@AppScope
@Inject
@Composable
fun AppBase(
    identityVM: IdentityVM,
    dialogViewModel: DialogViewModel,
    globalSettingManager: GlobalSettingManager,
    statisticVM: StatisticVM,
    orderBookViewModel: OrderBookViewModel,
    inventoryViewModel: InventoryViewModel,
    supplierViewModel: SupplierViewModel,
    purchaseOrderVM: PurchaseOrderVM,
    storeVM: StoreVM,
    navHostController: NavHostController = rememberNavController(),
) {

    var currentTheme by remember {
        mutableStateOf(
            CurrentTheme(
                globalSettingManager.darkMode,
                Color(globalSettingManager.currentColorSchemeId.toULong()),
                PaletteStyle.valueOf(globalSettingManager.currentPaletteStyle)
            )
        )
    }

    fun changeLanguage(language: String) {
        globalSettingManager.lang = when (language.uppercase()) {
            "DE" -> "DE"
            "ZH" -> "ZH"
            "NL" -> "DE"
            else -> globalSettingManager.dishFallBackLanguage
        }
        LibresSettings.languageCode = language
        changeLocale(Locale(language))
    }


    val scope = rememberCoroutineScope()
    LaunchedEffect(true) {
        globalDialogManager.coroutineScope = scope
    }
    fun resetDefaultLanguage() {
        scope.launch {
            changeLanguage(globalSettingManager.lang)
        }
    }
    LaunchedEffect(true) {
        displayCurrency = "€"
        resetDefaultLanguage()
    }

    fun goto(
        route: String, popTo: String = "", clearAllStack: Boolean = false, include: Boolean = false
    ) {
        navHostController.navigate(route) {
            if (popTo.isNotBlank()) {
                popUpTo(popTo) {
                    inclusive = include
                }
                launchSingleTop = true
            }
            if (clearAllStack) {
                popUpTo(0)
            }

        }
    }

    LaunchedEffect(identityVM.userLoadFinished to identityVM.currentUser) {
        if (identityVM.userLoadFinished) {
            if (identityVM.currentUser != null) {
                if (identityVM.userStoreList.isNotEmpty() && identityVM.userStoreList.any { it.deviceId == globalSettingManager.selectedDeviceId }) {
                    identityVM.enterStore(globalSettingManager.selectedDeviceId)
                    goto(startRoute, clearAllStack = true)

                } else {
                    goto(RouteName.ACTIVATE, clearAllStack = true)
                }

            } else {
                goto(RouteName.LOGIN, clearAllStack = true)
            }
        }
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    AadenMenuTheme(
        currentTheme = currentTheme
    ) {
        val urlProvider = remember {
            UrlProvider(
                resourceUrl = globalSettingManager.getResourceUrl(),
                dishImgUrl = globalSettingManager.getImgUrl(),
            )
        }
        val dialogManager = remember {
            globalDialogManager
        }

        var showLanguageDialog by remember {
            mutableStateOf(false)
        }
        CompositionLocalProvider(LocalTheme provides currentTheme) {
            CompositionLocalProvider(LocalUrl provides urlProvider) {
                CompositionLocalProvider(LocalDialogManager provides dialogManager) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = drawerState.isOpen,
                        drawerContent = {
                            ModalDrawerSheet {
                                SmallSpacer()
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = stringResource(Res.string._SystemSetting),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                SmallSpacer()

                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTheme.darkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                            contentDescription = ""
                                        )
                                    },
                                    label = { Text(stringResource(Res.string._DarkMode)) },
                                    selected = false,
                                    onClick = {
                                        scope.launch {
                                            currentTheme =
                                                currentTheme.copy(darkMode = !currentTheme.darkMode)
                                            globalSettingManager.darkMode = currentTheme.darkMode
                                            drawerState.close()
                                        }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )


                                SmallSpacer()

                                Row(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = stringResource(Res.string._ChangeTheme),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Spacer(Modifier.height(12.dp))
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(minSize = 64.dp),
                                    contentPadding = NavigationDrawerItemDefaults.ItemPadding
                                ) {
                                    items(colorsSets) {
                                        Surface(
                                            modifier = Modifier.padding(4.dp).clip(CircleShape),
                                            tonalElevation = 3.dp,
                                            onClick = {
                                                globalSettingManager.currentColorSchemeId =
                                                    (it.value).toString()
                                                currentTheme =
                                                    currentTheme.copy(currentColor = Color(it.value))
                                            }) {
                                            Box(
                                                modifier = Modifier.background(
                                                    if (it == currentTheme.currentColor) MaterialTheme.colorScheme.tertiaryContainer else Color.Transparent
                                                ).padding(4.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize()
                                                        .aspectRatio(1f).clip(
                                                            CircleShape
                                                        ).background(it)
                                                ) {}
                                            }


                                        }

                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(minSize = 108.dp),
                                    contentPadding = NavigationDrawerItemDefaults.ItemPadding
                                ) {
                                    items(PaletteStyle.entries.toTypedArray()) {
                                        Surface(
                                            modifier = Modifier.padding(4.dp),
                                            shape = MaterialTheme.shapes.large,
                                            tonalElevation = 3.dp,
                                            onClick = {
                                                globalSettingManager.currentPaletteStyle =
                                                    it.toString()
                                                currentTheme = currentTheme.copy(currentStyle = it)
                                            }) {
                                            Box(
                                                modifier = Modifier.background(
                                                    if (it == currentTheme.currentStyle) MaterialTheme.colorScheme.tertiaryContainer else Color.Transparent
                                                ).padding(8.dp), contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    it.name,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    maxLines = 1,
                                                    textAlign = TextAlign.Center,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }


                                        }

                                    }
                                }

                                Spacer(Modifier.height(12.dp))
                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text(stringResource(Res.string._Close)) },
                                    selected = false,
                                    onClick = {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )

                            }
                        }) {
                        NavHost(
                            navController = navHostController,
                            startDestination = RouteName.LOADING,
                        ) {
                            composable(RouteName.LOGIN) {
                                LoginPage(identityVM = identityVM, showLanguage = {
                                    showLanguageDialog = true
                                }, darkMode = currentTheme.darkMode, toggleDarkMode = {
                                    globalSettingManager.darkMode = !currentTheme.darkMode
                                    currentTheme =
                                        currentTheme.copy(darkMode = !currentTheme.darkMode)
                                }, openSetting = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                })
                            }
                            composable(RouteName.HOME) {

                                val uriHandler = LocalUriHandler.current

                                LaunchedEffect(identityVM.currentStore) {
                                    // Ensure the selected item is valid based on user auth
                                    val availableItems = NavigationItem.entries.filter {
                                        when (it) {
                                            NavigationItem.DataCenter -> identityVM.haveAuth(
                                                UserStoreAuth.DataCenter
                                            )

                                            NavigationItem.Inventory -> identityVM.haveAuth(
                                                UserStoreAuth.Inventory
                                            )

                                            NavigationItem.Supplier -> identityVM.haveAuth(
                                                UserStoreAuth.Supplier
                                            )

                                            NavigationItem.Workbench -> true // Workbench is always available
                                        }
                                    }
                                    if (storeVM.selectedNavigationItem !in availableItems) {
                                        storeVM.selectedNavigationItem =
                                            availableItems.firstOrNull() ?: NavigationItem.Workbench
                                    }
                                }

                                Scaffold(bottomBar = {
                                    NavigationBar {
                                        NavigationItem.entries.forEach { item ->
                                            // Conditionally display items based on user auth
                                            if (when (item) {
                                                    NavigationItem.DataCenter -> identityVM.haveAuth(
                                                        UserStoreAuth.DataCenter
                                                    )

                                                    NavigationItem.Inventory -> identityVM.haveAuth(
                                                        UserStoreAuth.Inventory
                                                    )

                                                    NavigationItem.Supplier -> identityVM.haveAuth(
                                                        UserStoreAuth.Supplier
                                                    )

                                                    NavigationItem.Workbench -> true
                                                }
                                            ) {
                                                NavigationBarItem(
                                                    selected = storeVM.selectedNavigationItem == item,
                                                    onClick = {
                                                        storeVM.selectedNavigationItem = item
                                                    },
                                                    icon = {
                                                        Icon(
                                                            item.icon,
                                                            contentDescription = item.label
                                                        )
                                                    },
                                                    label = { Text(item.label) }
                                                )
                                            }
                                        }
                                    }
                                }) { innerPadding ->
                                    Column(
                                        modifier = Modifier.consumeWindowInsets(innerPadding)
                                            .padding(innerPadding).fillMaxSize()
                                            .background(MaterialTheme.colorScheme.background),
                                    ) {
                                        when (storeVM.selectedNavigationItem) {
                                            NavigationItem.DataCenter -> DataCenterPage(
                                                identityVM = identityVM,
                                                storeVM = storeVM,
                                                toStatisticCenter = {
                                                    goto(RouteName.STATISTIC_CENTER)
                                                })

                                            NavigationItem.Inventory -> InventoryDashboard(
                                                identityVM = identityVM,
                                                inventoryViewModel = inventoryViewModel,
                                                toStorageItemList = {
                                                    inventoryViewModel.storageOperationType = null
                                                    goto(RouteName.INVENTORY_LIST)
                                                },
                                                toOrderList = {
                                                    if (it != null) {
                                                        purchaseOrderVM.selectedStatus = it
                                                        goto(RouteName.Supplier.ORDER_LIST)
                                                    } else {
                                                        storeVM.selectedNavigationItem =
                                                            NavigationItem.Supplier
                                                    }
                                                },
                                                toStorageOperation = {
                                                    inventoryViewModel.storageOperationType = it
                                                    goto(RouteName.INVENTORY_LIST)
                                                },
                                            )

                                            NavigationItem.Supplier -> SupplierListPage(
                                                inventoryViewModel = inventoryViewModel,


                                                orderBookViewModel = orderBookViewModel,
                                                toFindSupplierPage = {
                                                    goto(RouteName.Supplier.FIND_SUPPLIER)
                                                },
                                                toOrderListPage = {
                                                    purchaseOrderVM.selectedStatus =
                                                        OrderStatus.Active
                                                    goto(RouteName.Supplier.ORDER_LIST)
                                                },
                                                enterSupplierDetail = {
                                                    orderBookViewModel.chooseOrderBook(it.orderBook.id)
                                                    goto(RouteName.Supplier.ORDER_BOOK_DETAIL)
                                                })

                                            NavigationItem.Workbench -> WorkbenchPage(
                                                identityVM = identityVM,
                                                dialogViewModel = dialogViewModel,
                                                toAdminPage = {
                                                    uriHandler.openUri(
                                                        "https://admin.aaden.io?Base=" + getNgrokUrl(
                                                            globalSettingManager.selectedDeviceId
                                                        ).replace("https://", "")
                                                    )
                                                },
                                                onChangeTheme = {
                                                    scope.launch {
                                                        drawerState.open()
                                                    }
                                                },
                                                onLogOut = {
                                                    identityVM.logout()
                                                    goto(RouteName.LOGIN, clearAllStack = true)
                                                },
                                                toTeamManagePage = {
                                                    goto(RouteName.TeamManage)
                                                })
                                        }
                                    }
                                }
                            }
                            composable(RouteName.Supplier.FIND_SUPPLIER) {
                                FindSupplierPage(
                                    supplierViewModel,
                                    chooseSupplier = {
                                        dialogManager.confirmAnd(
                                            "您是否确定要绑定此供应商",
                                            "您选中的供应商" + it.name + "是公开供应商，可以直接绑定"
                                        ) {
                                            scope.launch {
                                                supplierViewModel.bindSupplier(it.id)
                                                navHostController.popBackStack()
                                            }
                                        }
                                    }
                                ) {
                                    navHostController.popBackStack()
                                }
                            }
                            composable(
                                RouteName.Supplier.ORDER_BOOK_DETAIL,
                            ) {
                                //显示供应商详情，过去对于这个供应商的订单，一个开始订购按钮，点击开始订购按钮，跳转到订购页面
                                OrderBookDetailPage(
                                    orderBookViewModel = orderBookViewModel,
                                    purchaseOrderVM = purchaseOrderVM,
                                    back = { navHostController.popBackStack() },
                                    toOrderProductPage = {
                                        goto(RouteName.Supplier.ORDER_MENU)
                                    },
                                    toOrderListPage = {
                                        goto(RouteName.Supplier.ORDER_LIST)
                                    },
                                    toOrderSignPage = {
                                        purchaseOrderVM.selectedOrderId = it
                                        goto(RouteName.Supplier.ORDER_SIGN)
                                    },
                                    toOrderDetailPage = {
                                        purchaseOrderVM.selectedOrderId = it
                                        goto(RouteName.Supplier.ORDER_DETAILS)
                                    })
                            }
                            composable(RouteName.Supplier.ORDER_MENU) {
                                //订购页面
                                OrderMenuPage(
                                    orderBookViewModel = orderBookViewModel,
                                    dialogViewModel = dialogViewModel,
                                    back = {
                                        navHostController.popBackStack()
                                    },
                                    toProductDetail = {
                                        orderBookViewModel.loadProductDetail(it)
                                        goto(RouteName.Supplier.PRODUCT_DETAIL)
                                    },
                                    toProductImport = {
                                        goto(RouteName.Supplier.PRODUCT_IMPORT)
                                    },
                                    toOrderConfirm = {
                                        goto(RouteName.Supplier.ORDER_CONFIRM)
                                    })
                            }



                            composable(RouteName.Supplier.PRODUCT_IMPORT) {
                                ProductImportPage(orderBookViewModel, back = {
                                    navHostController.popBackStack()
                                }, chooseCategory = {
                                    orderBookViewModel.chooseCategory(it)
                                    goto(RouteName.Supplier.PRODUCT_LIST)
                                }, save = {
                                    orderBookViewModel.saveImportResult()
                                    goto(
                                        RouteName.Supplier.ORDER_MENU,
                                        popTo = RouteName.Supplier.ORDER_MENU
                                    )
                                })
                            }
                            composable(RouteName.Supplier.PRODUCT_LIST) {
                                ProductListPage(orderBookViewModel, back = {
                                    navHostController.popBackStack()
                                }, toProductDetail = {
                                    orderBookViewModel.loadProductDetail(it)
                                    goto(RouteName.Supplier.PRODUCT_DETAIL)
                                }, save = {
                                    orderBookViewModel.saveImportResult()
                                    goto(
                                        RouteName.Supplier.ORDER_MENU,
                                        popTo = RouteName.Supplier.ORDER_MENU
                                    )
                                })
                            }
                            composable(RouteName.Supplier.PRODUCT_DETAIL) {
                                ProductDetailPage(orderBookViewModel) {
                                    navHostController.popBackStack()
                                }
                            }

                            composable(RouteName.Supplier.ORDER_CONFIRM) {
                                //确认下单页面，选择下单时间等
                                OrderConfirmPage(
                                    orderBookViewModel,
                                    toOrderSuccess = {
                                        goto(
                                            RouteName.Supplier.ORDER_SUCCESS,
                                            popTo = RouteName.Supplier.ORDER_BOOK_DETAIL
                                        )
                                    },
                                    back = {
                                        navHostController.popBackStack()
                                    },
                                )
                            }


                            composable(RouteName.Supplier.ORDER_SUCCESS) {
                                OrderSuccessPage() {
                                    navHostController.popBackStack()
                                }
                            }
                            composable(RouteName.Supplier.ORDER_LIST) {
                                OrderListPage(purchaseOrderVM = purchaseOrderVM, toOrderDetail = {
                                    purchaseOrderVM.selectedOrderId = it
                                    goto(RouteName.Supplier.ORDER_DETAILS)
                                }) {
                                    navHostController.popBackStack()
                                }
                            }
                            composable(RouteName.Supplier.ORDER_SIGN) {
                                OrderSignPage(
                                    purchaseOrderVM = purchaseOrderVM,
                                    dialogViewModel = dialogViewModel
                                ) {
                                    navHostController.popBackStack()
                                }
                            }
                            composable(RouteName.Supplier.ORDER_DETAILS) {
                                OrderDetailsPage(purchaseOrderVM, toOrderSignPage = {
                                    goto(RouteName.Supplier.ORDER_SIGN)
                                }) {
                                    navHostController.popBackStack()
                                }
                            }
                            composable(RouteName.LOADING) {
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                        .background(MaterialTheme.colorScheme.background),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            composable(RouteName.ACTIVATE) {
                                ActivatePage(identityVM) {
                                    identityVM.enterStore(deviceId = it)
                                    goto(RouteName.HOME, clearAllStack = true)
                                }
                            }
                            composable(RouteName.INVENTORY_LIST) {
                                StorageItemListPage(
                                    dialogViewModel = dialogViewModel,
                                    inventoryViewModel = inventoryViewModel
                                ) {
                                    navHostController.popBackStack()
                                }
                            }


                            composable(RouteName.STATISTIC_CENTER) {
                                StatisticCenterPage(statisticVM, identityVM = identityVM) {
                                    navHostController.popBackStack()
                                }
                            }

                            composable(RouteName.TeamManage) {
                                TeamManagePage(identityVM, dialogViewModel) {
                                    navHostController.popBackStack()
                                }
                            }
                        }
                    }
                    InputDialog(dialogViewModel.inputDialogRepository)

                    DateInputDialog(dialogViewModel)
                    ConfirmDialog()
                    SimpleSelectionDialog(dialogViewModel = dialogViewModel)
                    ChangeLanguageDialog(showLanguageDialog, { showLanguageDialog = false }) {
                        scope.launch {
                            changeLanguage(it)
                        }

                        showLanguageDialog = false
                    }
                    AddStoreDialog(identityVM)
                    StoreManagementDialog(identityVM) {
                        goto(RouteName.HOME, clearAllStack = true)
                    }
                    dialogViewModel.formSchemaList.forEach {
                        BaseFormDialog(it, dialogViewModel)
                    }

                    ComingSoonDialog(identityVM)
                    OfflineDialog(identityVM)
                    SuccessDialog()
                    LoadingDialog()


                }
            }
        }


    }
}


