package no.nav.pensjon.opptjening.popptestdata.common.exceptionhandling

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

inline fun requestRequirement(value: Boolean, message: () -> Any) {
    if (!value) {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, message().toString())
    }
}