package domain.composable.basic.keyboard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlinx.coroutines.launch
import modules.utils.FormatUtils.parsePriceString
import modules.utils.FormatUtils.toPriceDisplay


object KeyboardUtils {
    fun textAppender(currentText: String, appendText: String): String {
        return currentText + appendText
    }

    fun priceAppender(currentText: String, appendText: String): String {
        val currentNumber = try {
            currentText.parsePriceString()
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
        return (currentNumber + appendText.toBigDecimal()).toPriceDisplay()
    }
}

enum class IkKeyBoardType {
    Price, Number
}


@Composable
fun KeyboardWithDisplay(
    placeHolderText: String,
    type: IkKeyBoardType = IkKeyBoardType.Price,
    keyList: List<IkKey>,
    showNumber: Boolean = true,
    hideKeyboard: Boolean = false,
    outText: MutableState<String>? = null,
    keyboardClearCount: Int = 0,
    extraText: (@Composable RowScope.() -> Unit)? = null,
    textUpdated: suspend (text: String) -> Unit = {},
    captureKeyInput: suspend (currentText: String, key: String, id: Int) -> Pair<Boolean, String>,
) {

    var currentText by remember { outText ?: mutableStateOf("") }
    val scope = rememberCoroutineScope()

    suspend fun clearText() {
        currentText = ""
        textUpdated(currentText)
    }

    LaunchedEffect(key1 = placeHolderText, block = { clearText() })

    suspend fun keyboardInput(key: String, id: Int) {
        val (captured, text) = captureKeyInput(currentText, key, id)
        if (text != currentText) {
            currentText = text
        }
        if (!captured) {
            when (key) {
                "AC" -> {
                    clearText()
                }

                else -> {
                    currentText = when (type) {
                        IkKeyBoardType.Price -> KeyboardUtils.priceAppender(currentText, key)
                        IkKeyBoardType.Number -> KeyboardUtils.textAppender(currentText, key)
                    }
                }
            }
        }
        textUpdated(currentText)
    }
    Card {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val textContent =
                if (currentText.isNotBlank()) if (showNumber) currentText else "*".repeat(
                    currentText.length
                ) else placeHolderText
            val bigSpace = !showNumber && currentText.isNotBlank()
            Text(
                text = textContent,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    textAlign = if (showNumber) TextAlign.Start else TextAlign.Center,
                    letterSpacing = if (bigSpace) 24.sp else 0.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            if (extraText != null) {
                extraText()
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    if (!hideKeyboard) {
        KeyboardLayout(ikKeys = keyList, onInput = { it, id ->
            if (it.isNotBlank()) {
                scope.launch {
                    keyboardInput(it, id)
                }

            }
        })
    }


}