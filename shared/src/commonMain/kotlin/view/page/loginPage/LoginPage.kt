@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package view.page.loginPage

import LocalDialogManager
import shijiapp.shared.generated.resources.Res
import shijiapp.shared.generated.resources.tea_time
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
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
import domain.composable.autofill
import domain.composable.basic.button.MainActionGrowButton
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.wrapper.LoadingIndicator
import domain.composable.dialog.form.generateKeyboardType
import domain.user.IdentityVM
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import modules.utils.isValidEmail
import org.jetbrains.compose.resources.painterResource
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

            var email by remember { mutableStateOf("") }
            var loading by remember { mutableStateOf(false) }
            var emailConfirmed by remember { mutableStateOf(false) }
            var password by remember { mutableStateOf("") }
            var confirmPassword by remember { mutableStateOf("") }
            var isRegistered by remember { mutableStateOf(false) }

            var displayName by remember {
                mutableStateOf("")
            }

            var errorMessage by remember { mutableStateOf("") }
            var haveError by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painterResource(Res.drawable.memo),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16 / 9f),
                    contentScale = ContentScale.Fit
                )
                SmallSpacer()
                Text("每日食记", style = MaterialTheme.typography.titleLarge)

            }
            Column(modifier = Modifier.padding(16.dp, vertical = 8.dp)) {
                if (identityVM.userLoadFinished) {
                    val emailFocus = remember { FocusRequester() }
                    val passwordFocus = remember { FocusRequester() }
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                        },
                        label = { Text("Email") },
                        shape = MaterialTheme.shapes.large,
                        keyboardOptions = generateKeyboardType(
                            true,
                            keyboardType = KeyboardType.Email
                        ),
                        isError = haveError,
                        modifier = Modifier.fillMaxWidth().focusRequester(emailFocus).autofill(
                            listOf(
                                AutofillType.EmailAddress,
                                AutofillType.Username,
                            ),
                            onFill = {
                                email = it
                            }
                        ),
                        enabled = !emailConfirmed,
                        trailingIcon = if (emailConfirmed && isRegistered) {
                            {
                                Text(
                                    "忘记密码?",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 16.dp).clickable {
                                        scope.launch {
                                            manager.confirmAnd(
                                                title = "您不记得密码了吗？",
                                                content = "请不要担心，点击下面的确认按钮，我们会发送一个重置密码的邮件到您的邮箱里。",
                                            ) {
                                                scope.launch {
                                                    identityVM.sendPasswordResetEmail(email)
                                                }
                                                manager.confirmAnd(
                                                    "重置密码邮件已发送",
                                                    "请到邮箱查看，并重置密码"
                                                )
                                            }
                                        }
                                    })
                            }
                        } else {
                            {
                                Text(
                                    "匿名登录", style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 16.dp).clickable {
                                        scope.launch {
                                            manager.confirmAnd(
                                                title = "您确定要匿名登录吗？",
                                                content = "请注意，匿名登录有很多不方便的地方，" +
                                                        "例如：在登出后，或是在应用重新安装后，或在其他设备上，您将无法保存目前的状态。",
                                            ) {
                                                scope.launch {
                                                    loading = true
                                                    identityVM.logInAsGuest()
                                                    loading = false
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        },
                        supportingText = if (haveError) {
                            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
                        } else {
                            null
                        }
                    )
                    if (emailConfirmed) {
                        OutlinedTextField(
                            value = password,
                            shape = MaterialTheme.shapes.large,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = generateKeyboardType(
                                isRegistered,
                                keyboardType = KeyboardType.Password
                            ),
                            modifier = Modifier.fillMaxWidth().focusRequester(passwordFocus)
                                .autofill(
                                    autofillTypes = if (isRegistered) listOf(AutofillType.Password) else listOf(
                                        AutofillType.NewPassword
                                    ),
                                    onFill = {
                                        password = it
                                        if (!isRegistered) {
                                            confirmPassword = it
                                        }
                                    }
                                )
                        )
                        if (!isRegistered) {
                            OutlinedTextField(
                                value = confirmPassword,
                                shape = MaterialTheme.shapes.large,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = generateKeyboardType(
                                    false,
                                    keyboardType = KeyboardType.Password
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = displayName,
                                shape = MaterialTheme.shapes.large,
                                onValueChange = { displayName = it },
                                label = { Text("用户显示名称") },
                                keyboardOptions = generateKeyboardType(
                                    true,
                                    keyboardType = KeyboardType.Text
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    SmallSpacer()

                    LaunchedEffect(true) {
                        emailFocus.requestFocus()
                    }
                    Row() {
                        if (emailConfirmed) {
                            MainActionGrowButton(
                                "返回",
                                color = MaterialTheme.colorScheme.surfaceContainer,
                            ) {

                                emailConfirmed = false
                                isRegistered = false
                                password = ""
                                confirmPassword = ""

                            }
                            SmallSpacer()
                        }
                        MainActionGrowButton(
                            if (!emailConfirmed) "继续" else if (isRegistered) "登录" else "注册",
                            loading = loading,
                        ) {
                            loading = true
                            fun showError(message: String) {
                                haveError = message.isNotBlank()
                                errorMessage = message
                                loading = false
                            }
                            if (!emailConfirmed) {
                                if (email.isNotBlank() && email.isValidEmail()) {
                                    scope.launch {
                                        val result = identityVM.getLoginMethods(email)
                                        result.forEach { Napier.e { it } }
                                        emailConfirmed = true
                                        isRegistered = result.contains("password")
                                        delay(20)
                                        passwordFocus.requestFocus()
                                        loading = false
                                    }
                                } else {
                                    showError("请输入有效的邮箱地址")
                                }
                            } else {
                                if (isRegistered) {
                                    if (password.isNotBlank()) {
                                        scope.launch {
                                            val err = identityVM.loginWithPassword(email, password)
                                            showError(err)
                                        }
                                    } else {
                                        showError("密码不能为空")
                                    }
                                } else {
                                    if (password.isNotBlank() && confirmPassword == password) {
                                        scope.launch {
                                            val err = identityVM.registerWithPassword(
                                                email,
                                                password,
                                                displayName
                                            )
                                            showError(err)
                                        }
                                    } else {
                                        showError("密码不一致或密码为空")
                                    }
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

