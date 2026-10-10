package com.thelazybattley.dinkspace.core.presentation

import com.thelazybattley.dinkspace.core.domain.DataError
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.error_disk_full
import dinkspace.shared.generated.resources.error_no_internet
import dinkspace.shared.generated.resources.error_not_found
import dinkspace.shared.generated.resources.error_server
import dinkspace.shared.generated.resources.error_unauthorized
import dinkspace.shared.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

fun DataError.toStringResource(): StringResource = when (this) {
    DataError.Remote.NO_INTERNET -> Res.string.error_no_internet
    DataError.Remote.UNAUTHORIZED -> Res.string.error_unauthorized
    DataError.Remote.NOT_FOUND -> Res.string.error_not_found
    DataError.Remote.SERVER -> Res.string.error_server
    DataError.Local.DISK_FULL -> Res.string.error_disk_full
    DataError.Remote.UNKNOWN,
    DataError.Local.UNKNOWN -> Res.string.error_unknown
}
