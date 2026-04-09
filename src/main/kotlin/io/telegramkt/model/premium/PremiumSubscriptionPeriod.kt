package io.telegramkt.model.premium

sealed class PremiumSubscriptionPeriod(
    val monthCount: Int,
    val starCount: Int
) {
    object ThreeMonths : PremiumSubscriptionPeriod(3, 1000)
    object SixMonths : PremiumSubscriptionPeriod(6, 1500)
    object TwelveMonths : PremiumSubscriptionPeriod(12, 2500)

    fun fromMonths(months: Int): PremiumSubscriptionPeriod = when (months) {
        3 -> ThreeMonths
        6 -> SixMonths
        12 -> TwelveMonths
        else -> throw IllegalArgumentException("Invalid month count: $months. Allowed: 3, 6, 12")
    }
}