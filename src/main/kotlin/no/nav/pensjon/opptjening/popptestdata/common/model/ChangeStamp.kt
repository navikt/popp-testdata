package no.nav.pensjon.opptjening.popptestdata.common.model

import java.util.*

internal const val DEFAULT_CHANGED_BY = "POPPTESTDATA"

data class ChangeStamp(
    val createdBy: String = DEFAULT_CHANGED_BY,
    val createdDate: Date = Date(),
    val updatedBy: String = DEFAULT_CHANGED_BY,
    val updatedDate: Date = Date()
)