package modules.utils

import androidx.compose.ui.graphics.ImageBitmap
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import korlibs.image.format.cg.toByteArray
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGImageAlphaInfo
import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation

val numberFormat = NSNumberFormatter()
val currencyStyle = NSNumberFormatterCurrencyStyle
actual fun toPriceDisplay(value: BigDecimal): String {
    numberFormat.numberStyle = currencyStyle
    val number = NSDecimalNumber(value.toStringExpanded().toFloat())
    return numberFormat.stringFromNumber(number) ?: " - "
}

actual fun fromPriceDisplay(string: String): BigDecimal {
    return numberFormat.numberFromString(string)?.doubleValue?.toBigDecimal() ?: BigDecimal.ZERO
}

@OptIn(ExperimentalForeignApi::class)
fun ImageBitmap.toUIImage(): UIImage? {
    val width = this.width
    val height = this.height
    val buffer = IntArray(width * height)

    this.readPixels(buffer)

    val colorSpace = CGColorSpaceCreateDeviceRGB()
    val context = CGBitmapContextCreate(
        data = buffer.refTo(0),
        width = width.toULong(),
        height = height.toULong(),
        bitsPerComponent = 8u,
        bytesPerRow = (4 * width).toULong(),
        space = colorSpace,
        bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value
    )

    val cgImage = CGBitmapContextCreateImage(context)
    return cgImage?.let { UIImage.imageWithCGImage(it) }
}

actual fun ImageBitmap.toByteArray(): ByteArray {
    val uiImage=this.toUIImage()
    return uiImage?.let { UIImagePNGRepresentation(it)?.toByteArray() }!!
}