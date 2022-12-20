package no.nav.pensjon.opptjening.popptestdata.inntekt.model

data class HentSumPiResponse(val inntekter: List<SumPi> = listOf())

data class SumPi(val inntektAr: Int, val belop: Long, val inntektType: String?)