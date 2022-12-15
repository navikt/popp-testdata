package no.nav.pensjon.opptjening.popptestdata.common.model

import java.util.*

data class ChangeStamp(
    val createdBy: String = "POPPTESTDATA",
    val createdDate: Date = Date(),
    val updatedBy: String = "POPPTESTDATA",
    val updatedDate: Date = Date()
)