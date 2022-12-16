package no.nav.pensjon.opptjening.popptestdata.inntekt


data class LagreInntektRequest(
    val fnr: String,
    val fomAar: Int,
    val tomAar: Int,
    val belop: Long,
    val redusertMedGrunnbelop: Boolean = false
)