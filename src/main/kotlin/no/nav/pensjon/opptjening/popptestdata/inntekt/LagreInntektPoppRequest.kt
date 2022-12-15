package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.model.ChangeStamp

data class LagreInntektPoppRequest(
    val inntekt: Inntekt
)

data class Inntekt(
    val fnr: String,
    val inntektAr: Int,
    val belop: Long,
    val changeStamp: ChangeStamp = ChangeStamp(),
    val kilde: String = "PEN",
    val kommune: String = "1337",
    val inntektType: String = "INN_LON"
)
