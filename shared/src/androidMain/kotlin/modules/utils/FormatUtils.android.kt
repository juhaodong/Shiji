package modules.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.decimal.toJavaBigDecimal
import java.io.ByteArrayOutputStream
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale


private val priceFormat: NumberFormat = NumberFormat.getInstance(Locale.GERMANY).apply {
    val currency = Currency.getInstance(Locale.GERMANY)
    minimumFractionDigits = currency.defaultFractionDigits
}

actual fun toPriceDisplay(value: BigDecimal): String {
    val currency = displayCurrency
    return """${priceFormat.format(value.toJavaBigDecimal())} $currency"""
}

actual fun fromPriceDisplay(string: String): BigDecimal {
    return priceFormat.parse(string)?.toDouble()?.toBigDecimal()
        ?.roundToDigitPositionAfterDecimalPoint(
            2, com.ionspin.kotlin.bignum.decimal.RoundingMode.ROUND_HALF_CEILING
        ) ?: BigDecimal.ZERO
}

actual fun ImageBitmap.toByteArray(): ByteArray {
    val bitmap = this.asAndroidBitmap()
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}