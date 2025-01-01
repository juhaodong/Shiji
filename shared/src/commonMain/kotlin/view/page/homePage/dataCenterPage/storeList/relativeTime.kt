package view.page.homePage.dataCenterPage.storeList

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import nl.jacobras.humanreadable.HumanReadable

fun getRelativeTime(lastUpdate: LocalDateTime): String {
    return HumanReadable.timeAgo(lastUpdate.toInstant(TimeZone.currentSystemDefault())).trim()
}