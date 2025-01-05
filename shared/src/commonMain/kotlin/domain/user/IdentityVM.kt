@file:OptIn(ExperimentalUuidApi::class)

package domain.user


import VersionInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raedghazal.kotlinx_datetime_ext.now
import com.vanniktech.locale.Locale
import dataLayer.serivce.ActivationService
import dataLayer.serivce.FrontendLogDTO
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.messaging.messaging
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.form.TextFormField
import domain.food.user.UserProfile
import domain.food.user.UserProfileEditDTO
import domain.food.user.UserProfileService
import domain.inventory.InventoryRepository
import domain.supplier.model.ShopInfo
import domain.user.model.AcceptInviteRequestDTO
import io.github.aakira.napier.Napier
import io.github.skeptick.libres.LibresSettings
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.SafeRequestScope
import modules.utils.globalDialogManager
import kotlin.uuid.ExperimentalUuidApi


@AppScope
@Inject
class IdentityVM(
    val storeService: StoreService,
    val globalSettingManager: GlobalSettingManager,
    val activationService: ActivationService,
    val dialogViewModel: DialogViewModel,
    val inventoryRepository: InventoryRepository,
    val userProfileService: UserProfileService
) : ViewModel() {


    fun <R> withAuth(content: () -> R): R? {
        if (haveAuth()) {
            return content()
        }
        return null
    }


    var currentlyOffline by mutableStateOf(false)
    var showComingSoon by mutableStateOf(false)
    var showProfileDialog by mutableStateOf(false)

    var currentUser: FirebaseUser? by mutableStateOf(null)
    val fireBaseAuth = Firebase.auth


    var userLoadFinished by mutableStateOf(false)
    var userProfileLoading by mutableStateOf(false)
    fun toggleProfileDialog() {
        if (userProfileLoading) {
            return
        }
        viewModelScope.launch {
            if (!showProfileDialog) {
                userProfileLoading = true
                refreshProfile()
                userProfileLoading = false
            }


        }
        showProfileDialog = !showProfileDialog

    }

    suspend fun getLoginMethods(email: String): List<String> {
        return fireBaseAuth.fetchSignInMethodsForEmail(email)
    }

    suspend fun loginWithPassword(email: String, password: String): String {
        try {
            fireBaseAuth.signInWithEmailAndPassword(email, password)
            return ""
        } catch (e: Exception) {
            return e.message!!
        }
    }

    suspend fun registerWithPassword(email: String, password: String, displayName: String): String {
        return try {
            fireBaseAuth.createUserWithEmailAndPassword(email, password)
            if (currentUser != null) {
                currentUser?.updateProfile(displayName = displayName)
            }
            currentUser = fireBaseAuth.currentUser
            ""
        } catch (e: Exception) {
            e.message!!
        }
    }

    fun logout() {
        viewModelScope.launch {
            userLoadFinished = false
            fireBaseAuth.signOut()
            userLoadFinished = true
        }
    }

    suspend fun sendPasswordResetEmail(email: String) {
        return fireBaseAuth.sendPasswordResetEmail(email)
    }

    suspend fun logInAsGuest(): AuthResult {
        return fireBaseAuth.signInAnonymously()
    }


    var addingStore: ShopInfo? by mutableStateOf(null)
    private var currentOtp = ""
    suspend fun checkOpt(opt: String): Boolean {
        addingStore = null
        try {
            val res = storeService.getStoreDetailsByOtp(opt)

            addingStore = res
            currentOtp = opt
            return res != null
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    var bindingStore by mutableStateOf(false)
    var currentProfile by mutableStateOf<UserProfile?>(null)
    var updateUserProfileDialog by mutableStateOf(false)


    fun haveAuth(): Boolean {
        return true
    }


    fun bindStore() {
        viewModelScope.launch {
            try {
                bindingStore = true
                storeService.acceptInvite(
                    AcceptInviteRequestDTO(
                        activeCode = currentOtp,
                        deviceId = addingStore!!.shopId.toString(),
                        firebaseUid = currentUser!!.uid
                    )
                )
                refreshProfile()
                addingStore = null
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                bindingStore = false
            }
        }
    }


    private suspend fun refreshProfile() {

        if (currentUser != null) {
            currentProfile =
                SafeRequestScope.handleRequest { userProfileService.getUserProfile(currentUser!!.uid) }
        } else {
            currentProfile = null
        }
    }

    private var currentFCMToken = ""
    private suspend fun onUserUpdated(user: FirebaseUser?) {
        userLoadFinished = false
        currentUser = user
        Napier.e { "authStateChanged" }
        Napier.e { user?.email.toString() }
        refreshProfile()
        if (user != null) {
            try {
                currentFCMToken = Firebase.messaging.getToken()
                SafeRequestScope.handleRequest {
                    Napier.e {
                        "--->FCM TOKEN:$currentFCMToken"
                    }
                    val res =
                        storeService.updateToken(UpdateTokenRequest(user.uid, currentFCMToken))
                    Napier.e {
                        "-->$res"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            viewModelScope.launch {
                SafeRequestScope.handleRequest {
                    activationService.reportToCloud(
                        FrontendLogDTO(
                            name = user.displayName ?: user.email ?: user.uid,
                            ip = "",
                            uuid = user.uid,
                            version = VersionInfo.name,
                            deviceId = user.uid,
                            frontendType = "aaden-compose-admin",
                            timestamp = LocalDateTime.now()
                                .toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                        )
                    )
                }
            }
        }
        userLoadFinished = true
    }

    fun updateProfile(callBack: () -> Unit) {
        viewModelScope.launch {
            val profileEditDTO = dialogViewModel.showFormDialog<UserProfileEditDTO>(
                TextFormField(keyName = "nickname", label = "昵称"),
                TextFormField(
                    keyName = "birthDate",
                    label = "生日",
                    placeHolder = "请按照YYYY-MM-DD填写"
                ),
                TextFormField(
                    keyName = "exerciseIntensity",
                    label = "锻炼强度",
                    placeHolder = ""
                ),
                TextFormField(
                    keyName = "height",
                    label = "身高(cm)",
                    placeHolder = ""
                ),
                TextFormField(
                    keyName = "currentWeight",
                    label = "当前体重(kg)",
                    placeHolder = ""
                ),
                TextFormField(
                    keyName = "targetWeight",
                    label = "目标体重(kg)",
                    placeHolder = ""
                ),
                TextFormField(
                    keyName = "weightLossCycle",
                    label = "减重周期(天)",
                    placeHolder = ""
                ),
                title = "修改个人资料",
            )
            val profile = profileEditDTO.toUserProfile(currentUser!!.uid)
            SafeRequestScope.handleRequest {
                userProfileService.createOrUpdateUserProfile(profile)
            }
            refreshProfile()
            callBack()
        }

    }

    var qrHaveResult by mutableStateOf(false)
    var shopInfo by mutableStateOf<ShopInfo?>(null)
    var scanResult by mutableStateOf("")
    var bindLoading by mutableStateOf(false)
    fun bindMainDevice() {
        bindLoading = true

        viewModelScope.launch {
            if (currentUser?.isAnonymous == true) {
                globalDialogManager.confirmAnd("匿名用户不允许作为主用户")
            } else {
                val res = SafeRequestScope.handleRequest {
                    storeService.bindMainUser(scanResult, currentUser!!.uid)
                }

                if (res != null) {
                    refreshProfile()
                    updateUserProfileDialog = false
                    globalDialogManager.successAnd(
                        "" +
                                "绑定成功，作为门店的主用户，您可以邀请其他用户加入门店。" +
                                "请注意，每个门店只能由一个主用户，如果您弄丢了您的主用户账户，" +
                                "请联系我们的客服团队为您恢复。"
                    )

                } else {
                    globalDialogManager.confirmAnd(
                        "绑定失败",
                        "这可能是由于网络错误/您的门店已经被其他用户绑定等原因"
                    )
                }
            }
            bindLoading = false


        }
    }

    fun checkQrCode(code: String) {
        viewModelScope.launch {
            qrHaveResult = true
            val res = SafeRequestScope.handleRequest {
                storeService.getStoreInfoByUUID(code)
            }
            shopInfo = res

            scanResult = code
        }
    }

    fun updateUserName() {
        viewModelScope.launch {
            currentUser?.updateProfile(
                displayName =
                    dialogViewModel.showInput("请输入新的用户名")
            )
            currentUser = fireBaseAuth.currentUser
        }
    }

    fun updateImage(imageByteArray: ByteArray) {
        viewModelScope.launch {
            val imageUrl = inventoryRepository.uploadFile(imageByteArray)
            currentUser?.updateProfile(
                photoUrl = imageUrl
            )
            currentUser = fireBaseAuth.currentUser
        }
    }


    init {
        viewModelScope.launch {
            LibresSettings.languageCode = "zh"
            fireBaseAuth.authStateChanged.collect {
                onUserUpdated(it)
            }
        }

    }

    fun showComingSoonDialog() {
        showComingSoon = true
    }
}