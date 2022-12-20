package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.common.ChangeStamp

internal const val DEFAULT_KILDE = "PEN"
internal const val DEFAULT_KOMMUNE = "1337"
internal const val DEFAULT_INNTEKT_TYPE = "INN_LON"

data class LagreInntektPoppRequest(
    val inntekt: InntektPopp
)

data class InntektPopp(
    val fnr: String,
    val inntektAr: Int,
    val belop: Long,
    val kilde: String = DEFAULT_KILDE,
    val kommune: String = DEFAULT_KOMMUNE,
    val inntektType: String = DEFAULT_INNTEKT_TYPE,
    val changeStamp: ChangeStamp = ChangeStamp(),
)