@file:OptIn(ExperimentalUuidApi::class)

package domain.user


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.form.DateFormField
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
import modules.network.safe
import modules.utils.globalDialogManager
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@AppScope
@Inject
class IdentityVM(
    val dialogViewModel: DialogViewModel,
    val userProfileService: UserProfileService,
    val cloudUserService: CloudUserService,
    val globalSettingManager: GlobalSettingManager,
) : ViewModel() {


    var currentlyOffline by mutableStateOf(false)
    var showComingSoon by mutableStateOf(false)
    var showProfileDialog by mutableStateOf(false)

    var currentUser: CloudUser? by mutableStateOf(null)


    var userLoadFinished by mutableStateOf(false)



    var loggingIn by mutableStateOf(true)
    fun refreshUserInfo() {
        viewModelScope.launch {
            if (globalSettingManager.token.isNotBlank()) {
                val userInfo =
                    suspend {
                        cloudUserService.getUserInfoByToken(token = globalSettingManager.token)
                    }.safe()
                if (userInfo != null) {
                    onUserUpdated(userInfo)
                } else {
                    onUserUpdated(null)
                }

            }

        }
    }

    var otpInput by mutableStateOf("")
    var emailInput by mutableStateOf("")
    fun loginWithOTP() {
        viewModelScope.launch {
            val user = suspend {
                cloudUserService.loginUsingOTP(
                    request = OTPLoginRequest(
                        email = currentUser!!.email,
                        otp = otpInput
                    )
                )
            }.safe()
            globalSettingManager.token = user?.tokenValue ?: ""
            refreshUserInfo()
        }
    }

    fun sendOTPToEmail() {
        viewModelScope.launch {
            suspend {
                cloudUserService.sendOTP(email = currentUser!!.email)
            }.safe()
        }
    }

    fun logout() {
        viewModelScope.launch {
            userLoadFinished = false
            suspend {
                cloudUserService.logout(token = globalSettingManager.token)
            }.safe()
            globalSettingManager.token = ""
            onUserUpdated(null)
            userLoadFinished = true
        }
    }


    var currentProfile by mutableStateOf<UserProfile?>(null)

    private suspend fun refreshProfile() {

        if (currentUser != null) {
            currentProfile =
                SafeRequestScope.handleRequest { userProfileService.getUserProfile(currentUser!!.id) }
        } else {
            currentProfile = null
        }
    }


    private suspend fun onUserUpdated(user: CloudUser?) {
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
                DateFormField(
                    keyName = "birthDate",
                    defaultValue = currentProfile?.birthDate,
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
                    label = "减重周期(天>=30)",
                    placeHolder = "",
                    validator = {
                        (it?.toIntOrNull() ?: 0) >= 30
                    },
                    defaultValue = currentProfile?.weightLossCycle.toString()
                ),
                title = "修改个人资料",
            )
            val profile = profileEditDTO.toUserProfile(currentUser!!.id)
            SafeRequestScope.handleRequest {
                userProfileService.createOrUpdateUserProfile(profile)
            }
            refreshProfile()
            callBack()
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
        }
        refreshUserInfo()

    }

    fun requestDeleteAccount() {
        viewModelScope.launch {
            globalDialogManager.confirmDelete("您的用户账户") {
                viewModelScope.launch {
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
            "这里提供的膳食摄入建议是根据世卫组织体重BMI等数据计算，仅供参考，不构成医疗建议。" +
                    "参考链接：https://www.who.int/zh/news-room/fact-sheets/detail/healthy-diet"
        )
    }

    fun showComingSoonDialog() {
        showComingSoon = true
    }
}