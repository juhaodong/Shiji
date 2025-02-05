package domain.composable.basic.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun OTPLayout(
    title: String,
    checkOTP: suspend (String) -> Boolean,
    back: () -> Unit,
    otpLength: Int = 6,
    onSuccess: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var currentOTPValue by remember {
        mutableStateOf("")
    }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        currentOTPValue = ""
        loading = false
    }


    if (loading) {
        Surface(
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth().height((96 * 4).dp),
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    } else {
        Text(title)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 0..<otpLength) {
                val value = currentOTPValue.getOrNull(i)
                Surface(
                    modifier = Modifier.weight(1f).height(64.dp),
                    color = if (value != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Box(contentAlignment = Alignment.Center) {

                        Text(
                            (value ?: "-").toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        KeyboardLayout(
            columnCount = 3,
            ikKeys = listOf(
                "7", "8", "9",
                "4", "5", "6",
                "1", "2", "3",
                "<-", "0", IkKey("C", color = MaterialTheme.colorScheme.tertiaryContainer),
            ).toKeyList()
        ) { name, _ ->
            if (name == "C") {
                currentOTPValue = ""
            } else if (name == "<-") {
                back()
            } else {
                currentOTPValue += name
                scope.launch {
                    if (currentOTPValue.length == otpLength) {
                        loading = true
                        val result = checkOTP(currentOTPValue)

                        if (result) {
                            onSuccess(currentOTPValue)
                        }
                        currentOTPValue = ""
                        loading = false
                    }
                }
            }


        }
    }

}