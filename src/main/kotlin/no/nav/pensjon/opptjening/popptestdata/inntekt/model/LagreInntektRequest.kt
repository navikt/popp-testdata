package no.nav.pensjon.opptjening.popptestdata.inntekt.model

data class LagreInntektRequest(
    val fnr: String,
    val fomAar: Int,
    val tomAar: Int,
    val belop: Long,
    val redusertMedGrunnbelop: Boolean = false
)