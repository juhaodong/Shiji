@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
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
import com.raedghazal.kotlinx_datetime_ext.now
import domain.composable.basic.button.ActionLeftMainButton
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.dialog.ComingSoonDialog
import domain.composable.dialog.ConfirmDialog
import domain.composable.dialog.DateInputDialog
import domain.composable.dialog.LoadingDialog
import domain.composable.dialog.OfflineDialog
import domain.composable.dialog.SuccessDialog
import domain.composable.dialog.basic.BeautifulDialog
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.changeLanguageDialog.ChangeLanguageDialog
import domain.composable.dialog.form.BaseFormDialog
import domain.composable.dialog.inputDialog.InputDialog
import domain.composable.dialog.selection.SimpleSelectionDialog
import view.FoodLogDetailDialog

import domain.user.NutritionVM
import domain.user.IdentityVM
import io.github.skeptick.libres.LibresSettings
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.startRoute
import modules.physic.changeLocale
import modules.utils.closingTodayRange
import modules.utils.dateOnly
import modules.utils.display
import modules.utils.displayCurrency
import modules.utils.generateLast120Months
import modules.utils.generateYearsSince1970
import modules.utils.getNgrokUrl
import modules.utils.globalDialogManager
import modules.utils.toLocalDate
import org.jetbrains.compose.resources.stringResource
import shijiapp.shared.generated.resources.Res
import shijiapp.shared.generated.resources._ChangeTheme
import shijiapp.shared.generated.resources._Close
import shijiapp.shared.generated.resources._DarkMode
import shijiapp.shared.generated.resources._SystemSetting
import theme.AadenMenuTheme
import theme.CurrentTheme
import theme.colorsSets
import view.StoreManagementDialog
import view.page.activatePage.ActivatePage
import view.page.homePage.NavigationItem
import view.page.homePage.RecordPage
import view.page.homePage.dataCenterPage.DataCenterPage
import view.page.homePage.workbenchPage.WorkbenchPage
import view.page.loginPage.LoginPage

object RouteName {
    const val LOGIN = "login"
    const val ACTIVATE = "activate"
    const val HOME = "home"
    const val LOADING = "loading"
    const val INVENTORY_LIST = "inventoryList"

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
    nutritionVM: NutritionVM,
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
            changeLanguage("zh")
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
                if (identityVM.currentProfile != null) {
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

                                LaunchedEffect(identityVM.currentProfile) {
                                    // Ensure the selected item is valid based on user auth
                                    val availableItems = NavigationItem.entries

                                    if (nutritionVM.selectedNavigationItem !in availableItems) {
                                        nutritionVM.selectedNavigationItem =
                                            availableItems.firstOrNull() ?: NavigationItem.Workbench
                                    }
                                }

                                Scaffold(bottomBar = {
                                    NavigationBar {
                                        NavigationItem.entries.forEach { item ->

                                            NavigationBarItem(
                                                selected = nutritionVM.selectedNavigationItem == item,
                                                onClick = {
                                                    nutritionVM.selectedNavigationItem = item
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
                                }) { innerPadding ->
                                    Column(
                                        modifier = Modifier.consumeWindowInsets(innerPadding)
                                            .padding(innerPadding).fillMaxSize()
                                            .background(MaterialTheme.colorScheme.background),
                                    ) {
                                        when (nutritionVM.selectedNavigationItem) {
                                            NavigationItem.DataCenter -> DataCenterPage(
                                                identityVM = identityVM,
                                                nutritionVM = nutritionVM,
                                                toStatisticCenter = {

                                                })

                                            NavigationItem.DailyRecord -> RecordPage(
                                                identityVM = identityVM,
                                                nutritionVM
                                            )


                                            NavigationItem.Workbench -> WorkbenchPage(
                                                identityVM = identityVM,
                                                dialogViewModel = dialogViewModel,
                                                nutritionVM = nutritionVM,
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
                                    goto(RouteName.HOME, clearAllStack = true)
                                }
                            }




                        }
                    }
                    InputDialog(dialogViewModel.inputDialogRepository)
                    BeautifulDialog(
                        show = nutritionVM.showDateDialog,
                        onDismissRequest = { nutritionVM.showDateDialog = false },
                        useCloseButton = false,
                        noPadding = true
                    ) {
                        val timeOption = listOf("日", "月", "年", "其他")
                        var selectTab by remember { mutableStateOf(timeOption[0]) }
                        var dateRange by remember { mutableStateOf(closingTodayRange()) }
                        LaunchedEffect(true) {
                            dateRange = closingTodayRange()
                        }
                        PrimaryTabRow(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            selectedTabIndex = timeOption.indexOf(selectTab)
                        ) {
                            timeOption.forEach { s ->
                                Tab(
                                    s == selectTab,
                                    onClick = { selectTab = s },
                                    text = { Text(s, style = MaterialTheme.typography.bodyMedium) })
                            }
                        }
                        when (selectTab) {
                            "日" -> {
                                val datePickerState =
                                    rememberDatePickerState(
                                        initialSelectedDateMillis = Clock.System.now()
                                            .toEpochMilliseconds(),
                                        selectableDates = object : SelectableDates {
                                            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                                return Instant.fromEpochMilliseconds(utcTimeMillis)
                                                    .toLocalDateTime(
                                                        TimeZone.currentSystemDefault()
                                                    ) <= LocalDateTime.now()
                                            }
                                        })
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                                ) { SmallSpacer(16) }

                                DatePicker(
                                    state = datePickerState, showModeToggle = false, title = null
                                )
                                LaunchedEffect(datePickerState.selectedDateMillis) {
                                    val date = datePickerState.selectedDateMillis.toLocalDate()
                                    if (date != null) {
                                        dateRange = date to date
                                    }
                                }
                            }

                            "月" -> {
                                val monthList = generateLast120Months()
                                var selectedDateRange by remember { mutableStateOf(monthList[0]) }
                                LaunchedEffect(true) {
                                    selectedDateRange = monthList[0]
                                }
                                LazyColumn(
                                    modifier = Modifier.height(400.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(
                                        vertical = 8.dp,
                                        horizontal = 16.dp
                                    )
                                ) {
                                    items(monthList) {
                                        Surface(
                                            shape = MaterialTheme.shapes.medium,
                                            modifier = Modifier.fillMaxWidth(),
                                            onClick = {
                                                selectedDateRange = it
                                            },
                                            color = if (selectedDateRange == it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.Bottom
                                            ) {
                                                Text(
                                                    it.display()
                                                )
                                                GrowSpacer()
                                                Text(
                                                    it.first.dateOnly() + " - " + it.second.dateOnly(),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                    }
                                }
                                LaunchedEffect(selectedDateRange) {
                                    dateRange = selectedDateRange
                                }

                            }

                            "年" -> {
                                val yearList = generateYearsSince1970()
                                var selectedDateRange by remember { mutableStateOf(yearList[0]) }
                                LaunchedEffect(true) {
                                    selectedDateRange = yearList[0]
                                }
                                LazyColumn(
                                    modifier = Modifier.height(400.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(
                                        vertical = 8.dp,
                                        horizontal = 16.dp
                                    )
                                ) {
                                    items(yearList) {
                                        Surface(
                                            shape = MaterialTheme.shapes.medium,
                                            modifier = Modifier.fillMaxWidth(),
                                            onClick = {
                                                selectedDateRange = it
                                            },
                                            color = if (selectedDateRange == it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface

                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.Bottom
                                            ) {
                                                Text(
                                                    it.display()
                                                )
                                                GrowSpacer()
                                                Text(
                                                    it.first.dateOnly() + " - " + it.second.dateOnly(),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                    }
                                }
                                LaunchedEffect(selectedDateRange) {
                                    dateRange = selectedDateRange
                                }
                            }

                            "其他" -> {
                                val datePickerState = rememberDateRangePickerState(
                                    initialSelectedStartDateMillis = Clock.System.now()
                                        .toEpochMilliseconds(),
                                    initialSelectedEndDateMillis = Clock.System.now()
                                        .toEpochMilliseconds(),
                                    selectableDates = object : SelectableDates {
                                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                            return Instant.fromEpochMilliseconds(utcTimeMillis)
                                                .toLocalDateTime(
                                                    TimeZone.currentSystemDefault()
                                                ) <= LocalDateTime.now()
                                        }
                                    })
                                DateRangePicker(
                                    state = datePickerState,
                                    showModeToggle = false,
                                    title = {},
                                    modifier = Modifier.weight(1f)
                                )
                                LaunchedEffect(datePickerState.selectedStartDateMillis to datePickerState.selectedEndDateMillis) {

                                    val dateStart =
                                        datePickerState.selectedStartDateMillis.toLocalDate()
                                    val dateEnd =
                                        datePickerState.selectedEndDateMillis.toLocalDate()
                                    if (dateStart != null && dateEnd != null) {
                                        dateRange = dateStart to dateEnd
                                    }

                                }
                            }
                        }

                        Row(modifier = Modifier.padding(16.dp)) {
                            ActionLeftMainButton(text = "确认", icon = Icons.Default.Done) {
                                nutritionVM.confirmDateRange(dateRange)
                            }
                        }


                    }
                    DateInputDialog(dialogViewModel)
                    ConfirmDialog()
                    SimpleSelectionDialog(dialogViewModel = dialogViewModel)
                    ChangeLanguageDialog(showLanguageDialog, { showLanguageDialog = false }) {
                        scope.launch {
                            changeLanguage(it)
                        }

                        showLanguageDialog = false
                    }
                    FoodLogDetailDialog(nutritionVM)
                    StoreManagementDialog(identityVM)
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


