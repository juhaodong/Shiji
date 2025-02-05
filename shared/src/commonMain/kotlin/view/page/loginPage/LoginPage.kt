@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package view.page.loginPage

import LocalDialogManager
import shijiapp.shared.generated.resources.Res
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import domain.composable.DarkModeDisplay
import domain.composable.autofill
import domain.composable.basic.button.MainActionGrowButton
import domain.composable.basic.keyboard.OTPLayout
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.wrapper.LoadingIndicator
import domain.composable.dialog.form.generateKeyboardType
import domain.user.IdentityVM
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import modules.utils.isValidEmail
import org.jetbrains.compose.resources.painterResource
import qrgenerator.qrkitpainter.email
import shijiapp.shared.generated.resources.memo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    identityVM: IdentityVM,
    showLanguage: () -> Unit,
    openSetting: () -> Unit,
    darkMode: Boolean,
    toggleDarkMode: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val manager = LocalDialogManager.current
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {},
                navigationIcon = {
                    IconButton(onClick = { openSetting() }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    DarkModeDisplay(toggleDarkMode, darkMode)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding)
                .imePadding()
                .fillMaxSize(),
        ) {
            GrowSpacer()
            Column(modifier = Modifier.padding(16.dp, vertical = 8.dp)) {
                if (identityVM.userLoadFinished) {
                    if (identityVM.emailConfirmed) {

                        OTPLayout(
                            "您的登录验证码已经发送到您的邮箱，请在60秒内输入验证码",
                            back = {
                                identityVM.reset()
                            },
                            checkOTP = {
                                identityVM.loginWithOTP(it)
                            }) {
                            identityVM.refreshUserInfo()
                        }

                    } else {
                        val emailFocus = remember { FocusRequester() }
                        OutlinedTextField(
                            value = identityVM.emailInput,
                            onValueChange = {
                                identityVM.emailInput = it
                            },
                            label = { Text("Email") },
                            shape = MaterialTheme.shapes.large,
                            keyboardOptions = generateKeyboardType(
                                true,
                                keyboardType = KeyboardType.Email
                            ),
                            isError = identityVM.haveError,
                            modifier = Modifier.fillMaxWidth().focusRequester(emailFocus).autofill(
                                listOf(
                                    AutofillType.EmailAddress,
                                    AutofillType.Username,
                                ),
                                onFill = {
                                    identityVM.emailInput = it
                                }
                            ),
                            enabled = !identityVM.emailConfirmed,
                            supportingText = if (identityVM.haveError) {
                                {
                                    Text(
                                        identityVM.errorMessage,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            } else {
                                null
                            }
                        )
                        SmallSpacer()

                        LaunchedEffect(true) {
                            emailFocus.requestFocus()
                        }
                        Row {
                            if (identityVM.emailConfirmed) {
                                MainActionGrowButton(
                                    "返回",
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                ) {
                                    identityVM.reset()


                                }
                                SmallSpacer()
                            }
                            MainActionGrowButton(
                                if (!identityVM.emailConfirmed) "继续" else "登录",
                                loading = identityVM.loading,
                            ) {


                                if (!identityVM.emailConfirmed) {
                                    identityVM.sendOTPToEmail()
                                }
                            }
                        }
                    }


                } else {
                    LoadingIndicator()
                }

            }
        }
    }
}

