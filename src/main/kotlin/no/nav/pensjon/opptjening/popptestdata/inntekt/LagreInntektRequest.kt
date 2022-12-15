package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.environment.Environment

data class LagreInntektRequest(
    val environment: Environment,
    val fnr: String,
    val fomAar: Int,
    val tomAar: Int,
    val belop: Long,
    val redusertMedGrunnbelop: Boolean = false
) {
    override fun toString(): String {
        return "Environment: $environment Fom: $fomAar Tom: $tomAar Belop: $belop Nedjustering: $redusertMedGrunnbelop"
    }
}