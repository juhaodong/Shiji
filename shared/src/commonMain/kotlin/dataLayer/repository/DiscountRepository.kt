package dataLayer.repository

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import me.tatarka.inject.annotations.Inject


@Inject
class DiscountRepository {
    private class Mod(discountStr: String) {
        var multiplyMod: BigDecimal
        var subtractMod: BigDecimal

        init {
            multiplyMod = BigDecimal.ZERO
            subtractMod = BigDecimal.ZERO
            if (discountStr != "") {
                if (discountStr.endsWith("p")) {
                    val percentage: BigDecimal =
                        BigDecimal.parseString(
                            discountStr.substring(
                                0,
                                discountStr.length - 1
                            )
                        ) * BigDecimal.parseString("0.01")
                    multiplyMod = percentage
                } else {
                    subtractMod = BigDecimal.parseString(discountStr)
                }
            }
        }

        @Throws(Exception::class)
        fun applyMod(before: BigDecimal): BigDecimal {
            var temp: BigDecimal = BigDecimal.ZERO
            temp = if (multiplyMod > BigDecimal.ZERO) {
                before * multiplyMod
            } else {
                temp.add(subtractMod)
            }

            if (temp < BigDecimal.ZERO) {
                throw Exception("Bigger than origin")
            }
            return (before - temp.roundToDigitPositionAfterDecimalPoint(
                2,
                RoundingMode.ROUND_HALF_CEILING
            )).roundToDigitPositionAfterDecimalPoint(
                2,
                RoundingMode.ROUND_HALF_CEILING
            )
        }
    }

    companion object {
        fun isValidDiscountStr(discountStr: String): Boolean {
            return discountStr.matches(Regex("^([0-9]+(\\.[0-9]+)?)?((p)+([kg])?)?$"))
        }

        /**
         * 返回的是折后价格
         */
        fun applyDiscountMod(total: BigDecimal, discountStr: String): BigDecimal {
            return if (isValidDiscountStr(discountStr)) {
                Mod(discountStr).applyMod(total)
            } else {
                total
            }
        }
    }

}