package com.thelazybattley.dinkspace.navigation

import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.ic_courts
import dinkspace.shared.generated.resources.ic_open_play
import dinkspace.shared.generated.resources.ic_partners
import dinkspace.shared.generated.resources.ic_record
import dinkspace.shared.generated.resources.nav_courts
import dinkspace.shared.generated.resources.nav_open_play
import dinkspace.shared.generated.resources.nav_partners
import dinkspace.shared.generated.resources.nav_record
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/** Tabs shown in the bottom navigation bar. */
enum class TopLevelDestination(
    val route: Route,
    val label: StringResource,
    val icon: DrawableResource,
) {
    COURTS(Route.Courts, Res.string.nav_courts, Res.drawable.ic_courts),
    OPEN_PLAY(Route.OpenPlay, Res.string.nav_open_play, Res.drawable.ic_open_play),
    PARTNERS(Route.Partners, Res.string.nav_partners, Res.drawable.ic_partners),
    RECORD(Route.Record, Res.string.nav_record, Res.drawable.ic_record),
}
