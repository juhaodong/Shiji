package modules.utils


import androidx.compose.ui.graphics.ImageBitmap
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.raedghazal.kotlinx_datetime_ext.LocalDateTimeFormatter
import com.raedghazal.kotlinx_datetime_ext.Locale
import com.raedghazal.kotlinx_datetime_ext.minus
import com.raedghazal.kotlinx_datetime_ext.now
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import nl.jacobras.humanreadable.HumanReadable
import kotlin.experimental.ExperimentalTypeInference
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

var displayCurrency = "€"
expect fun toPriceDisplay(value: BigDecimal): String

expect fun fromPriceDisplay(string: String): BigDecimal

fun BigDecimal.toPercentageDisplay(): String {
    return this.multiply(100.toBigDecimal())
        .roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_TOWARDS_ZERO)
        .toPlainString() + "%"
}

fun LocalDateTime.beautify(): String {
    val formatter = LocalDateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.default())
    return formatter.format(this)
}

fun LocalDateTime.toMinuteDisplay(): String {
    val formatter = LocalDateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.default())
    return formatter.format(this)
}

fun LocalDateTime.toHourDisplay(): String {
    val formatter = LocalDateTimeFormatter.ofPattern("yyyy-MM-dd HH", Locale.default())
    return formatter.format(this)
}


fun LocalDateTime.timeToNow(): String {
    return HumanReadable.timeAgo(this.toInstant(TimeZone.currentSystemDefault()))
}

val timeFormatter = LocalDateTimeFormatter.ofPattern("HH:mm", Locale.default())
fun LocalDateTime.timeOnly(): String {
    return timeFormatter.format(this)
}

val dateFormatter = LocalDateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.default())
fun LocalDateTime.dateOnly(): String {
    return dateFormatter.format(this)
}

val weekDayFormatter = LocalDateTimeFormatter.ofPattern("yyyy-MM-dd (EEEE)", Locale.default())

fun closingToday(): LocalDate {
    return LocalDateTime.now().minus(4, DateTimeUnit.HOUR).date
}

fun closingTodayRange(): Pair<LocalDate, LocalDate> {
    return closingToday() to closingToday()
}

fun LocalDate.dateOnly(): String {
    return dateFormatter.format(this)
}


fun LocalDate.dateWithWeekDay(): String {
    return weekDayFormatter.format(this)
}


fun Long?.toLocalDateTime(): LocalDateTime? {
    return this?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
    }
}

fun LocalDateTime.toLocalDate(): LocalDate {
    return LocalDate(this.year, this.monthNumber, this.dayOfMonth)
}

fun Pair<LocalDate, LocalDate>.display(): String {
    val month = generateLast120Months()
    val years = generateYearsSince1970()
    if (this in month) {
        return this.first.year.toString() + "/" + this.first.month.number.toString()
            .padStart(2, '0')
    }
    if (this in years) {
        return this.first.year.toString()
    }
    if (this.first != this.second) {
        return "${this.first.dateOnly()} - ${this.second.dateOnly()}"
    } else {
        return this.first.dateOnly()
    }
}

enum class DateRangeMoveDirection {
    Forward,
    Backward
}

fun Pair<LocalDate, LocalDate>.move(direction: DateRangeMoveDirection): Pair<LocalDate, LocalDate> {
    val factor = if (direction == DateRangeMoveDirection.Forward) 1 else -1
    val month = generateLast120Months()
    val years = generateYearsSince1970()
    if (this in month) {
        val currentIndex = month.indexOf(this)
        if ((currentIndex - factor) in month.indices) {
            return month[currentIndex - factor]
        } else {
            return this
        }
    } else if (this in years) {
        val currentIndex = years.indexOf(this)
        if ((currentIndex - factor) in years.indices) {
            return years[currentIndex - factor]
        } else {
            return this
        }
    } else {
        val daysToMove = (this.first.daysUntil(this.second).coerceAtLeast(1) * factor)
        return this.copy(
            first = this.first.plus(daysToMove, DateTimeUnit.DAY),
            second = this.second.plus(daysToMove, DateTimeUnit.DAY)
        )
    }

}


fun Pair<LocalDate, LocalDate>.withFinanceDisplay(): String {

    return "${this.first.dateOnly()} 04:00:00 - ${
        this.second.plus(1, DateTimeUnit.DAY).dateOnly()
    } 03:59:59"


}

fun Long?.toLocalDate(): LocalDate? {
    return this?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
            .toLocalDate()
    }
}

fun String.isValidEmail(): Boolean {
    val emailAddressRegex = Regex(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"
    )


    return this.matches(emailAddressRegex)
}

fun generateLast120Months(): List<Pair<LocalDate, LocalDate>> {
    val today = LocalDate.now()
    return (0..119).map { monthOffset ->
        val startDate = today.minus(DatePeriod(months = monthOffset))
            .minus(DatePeriod(days = today.dayOfMonth - 1))
        val endDate = if (monthOffset == 0) {
            today // Set end date to today for the current month
        } else {
            startDate.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
        }
        Pair(startDate, endDate)
    }
}

fun generateYearsSince1970(): List<Pair<LocalDate, LocalDate>> {
    val currentYear = LocalDate.now().year
    val today = LocalDate.now()
    return (1970..currentYear).map { year ->
        val startDate = LocalDate(year, 1, 1)
        val endDate = if (year == currentYear) {
            today // Set end date to today for the current year
        } else {
            LocalDate(year, 12, 31)
        }
        Pair(startDate, endDate)
    }.reversed()
}

object FormatUtils {

    const val times = "×"
    const val close = "✖"
    fun formatTime(string: String): Instant? {
        return try {
            string.ifBlank { return null }
            Instant.parse(string)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getCurrentTime(): Instant {
        return Clock.System.now()
    }

    fun secondToTimeDisplay(second: Int): String {
        val duration = (second).seconds.toComponents { minutes, seconds, _ ->
            minutes.toString().padStart(2, '0') + ":" + seconds.toString().padStart(2, '0')
        }
        return duration
    }

    fun BigDecimal.toPriceDisplay(): String {
        return toPriceDisplay(this)
    }

    fun BigDecimal.displayWithUnit(unit: String = ""): String {
        return this.copy(decimalMode = DecimalMode.US_CURRENCY).toPlainString() + unit
    }


    fun BigDecimal.displayWhenNotZero(): String {
        return if (this.compareTo(BigDecimal.ZERO) != 0) this.toPriceDisplay() else ""
    }

    fun String.parsePriceString(): BigDecimal {
        return fromPriceDisplay(this)
    }

    @OptIn(ExperimentalTypeInference::class)
    @kotlin.jvm.JvmName("sumOfBigDecimal")
    @OverloadResolutionByLambdaReturnType
    inline fun <T> Iterable<T>.sumOfB(selector: (T) -> BigDecimal): BigDecimal {
        var sum: BigDecimal = BigDecimal.ZERO.roundToDigitPositionAfterDecimalPoint(
            2, roundingMode = RoundingMode.ROUND_HALF_CEILING
        )
        for (element in this) {
            sum += selector(element)
        }
        return sum
    }


    fun List<String>.getExtraKeys(): List<String> {
        val list = this.map { it.uppercase().replace(Regex("\\d"), "").replace(".", "") }
        val extraKeyboardMap = mutableMapOf<Char, Int>()
        list.forEach { str ->
            str.forEach {
                if (extraKeyboardMap.contains(it)) {
                    extraKeyboardMap[it] = extraKeyboardMap.get(it)!! + 1
                } else {
                    extraKeyboardMap[it] = 1
                }
            }
        }
        val result = extraKeyboardMap.entries.sortedByDescending { it.value }.map {
            it.key
        }.take(8)
        if (result.isEmpty()) {
            return listOf()
        }
        if (result.size <= 4) {
            return arrayOfNulls<String>(4).mapIndexed { it, _ ->
                result.getOrNull(it)?.toString() ?: ""
            }
        } else {
            return arrayOfNulls<String>(8).mapIndexed { it, _ ->
                result.getOrNull(it)?.toString() ?: ""
            }
        }
    }

}


object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal.parseString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toPlainString())
    }

}

@OptIn(ExperimentalUuidApi::class)
object UUIDSerializer : KSerializer<Uuid> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Uuid) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uuid {
        return Uuid.parse(decoder.decodeString())
    }
}


fun getNgrokUrl(deviceId: String): String {
    return "https://ik" + deviceId.padStart(4, '0') + ".ngrok.aaden.io"
}

fun getEndpointUrl(deviceId: String): String {
    return getNgrokUrl(deviceId) + "/PHP/"
}

fun BigDecimal.easyDivide(other: BigDecimal): BigDecimal {
    return divide(other.coerceAtLeast(BigDecimal.ONE), DecimalMode.US_CURRENCY)
}

expect fun ImageBitmap.toByteArray(): ByteArray
