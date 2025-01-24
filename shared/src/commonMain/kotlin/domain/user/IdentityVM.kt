@file:OptIn(ExperimentalUuidApi::class)

package domain.user


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.form.OptionFormField
import domain.composable.dialog.form.TextFormField
import domain.composable.dialog.selection.SelectOption
import domain.food.service.UserProfile
import domain.food.service.UserProfileEditDTO
import domain.food.service.UserProfileService
import io.github.aakira.napier.Napier
import io.github.skeptick.libres.LibresSettings
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.SafeRequestScope
import modules.utils.dateOnly
import modules.utils.globalDialogManager
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@AppScope
@Inject
class IdentityVM(


    val dialogViewModel: DialogViewModel,

    val userProfileService: UserProfileService,
    val globalSettingManager: GlobalSettingManager,
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


    private var currentOtp = ""

    var bindingStore by mutableStateOf(false)
    var currentProfile by mutableStateOf<UserProfile?>(null)
    var updateUserProfileDialog by mutableStateOf(false)


    fun haveAuth(): Boolean {
        return true
    }


    private suspend fun refreshProfile() {

        if (currentUser != null) {
            currentProfile =
                SafeRequestScope.handleRequest { userProfileService.getUserProfile(currentUser!!.uid) }
        } else {
            currentProfile = null
        }
    }


    private suspend fun onUserUpdated(user: FirebaseUser?) {
        userLoadFinished = false
        currentUser = user
        Napier.e { "authStateChanged" }
        Napier.e { user?.email.toString() }
        refreshProfile()

        userLoadFinished = true
    }

    fun updateProfile(callBack: () -> Unit) {
        viewModelScope.launch {
            val profileEditDTO = dialogViewModel.showFormDialog<UserProfileEditDTO>(
                TextFormField(
                    keyName = "nickname",
                    label = "昵称",
                    defaultValue = currentProfile?.nickname
                ),
                TextFormField(
                    keyName = "birthDate",
                    defaultValue = currentProfile?.birthDate?.dateOnly(),
                    label = "生日",
                    placeHolder = "请按照YYYY-MM-DD填写"
                ),
                OptionFormField(
                    keyName = "exerciseIntensity", "锻炼强度", options = listOf(
                        SelectOption("剧烈(每周15小时)", value = 3),
                        SelectOption("中等(每周3-5小时)", value = 2),
                        SelectOption("轻度(每周1-3小时)", value = 1),
                    ),
                    defaultValue = currentProfile?.exerciseIntensity
                ),

                TextFormField(
                    keyName = "height",
                    label = "身高(cm)",
                    placeHolder = "",
                    defaultValue = currentProfile?.height.toString()
                ),
                TextFormField(
                    keyName = "currentWeight",
                    label = "当前体重(kg)",
                    placeHolder = "",
                    defaultValue = currentProfile?.currentWeight.toString()
                ),
                TextFormField(
                    keyName = "targetWeight",
                    label = "目标体重(kg)",
                    placeHolder = "",
                    defaultValue = currentProfile?.targetWeight.toString()
                ),
                TextFormField(
                    keyName = "weightLossCycle",
                    label = "减重周期(天)",
                    placeHolder = "",
                    defaultValue = currentProfile?.weightLossCycle.toString()
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
            val imageUrl = uploadFile(imageByteArray)
            currentUser?.updateProfile(
                photoUrl = imageUrl
            )
            currentUser = fireBaseAuth.currentUser
        }
    }

    suspend fun uploadFile(byteArray: ByteArray): String? {


        return SafeRequestScope.handleRequest {
            val multipart = MultiPartFormDataContent(formData {
                append("description", "Image")
                append("file", byteArray, Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(
                        HttpHeaders.ContentDisposition,
                        "filename=" + Uuid.random().toHexString() + ".png"
                    )
                })
            })

            userProfileService.uploadFile(multipart)
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

    fun requestDeleteAccount() {
        viewModelScope.launch {
            globalDialogManager.confirmDelete("您的用户账户") {
                viewModelScope.launch {

                    fireBaseAuth.currentUser?.delete()
                    logout()
                }

            }
        }
    }

    fun showOnceHealthAdvice() {
        if (globalSettingManager.showHealthAdvice) {
            showHealthAdvice()
            globalSettingManager.showHealthAdvice = false
        }
    }

    fun showHealthAdvice() {
        globalDialogManager.confirmAnd(
            "请注意",
            "这里提供的膳食摄入建议是根据世卫组织体重BMI等数据计算，仅供参考，不构成医疗建议。"
        )
    }

    fun showComingSoonDialog() {
        showComingSoon = true
    }
}