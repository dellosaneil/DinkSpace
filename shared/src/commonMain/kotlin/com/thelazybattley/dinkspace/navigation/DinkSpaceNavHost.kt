package com.thelazybattley.dinkspace.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.thelazybattley.dinkspace.feature.courts.presentation.detail.CourtDetailRoot
import com.thelazybattley.dinkspace.feature.courts.presentation.list.CourtsRoot
import com.thelazybattley.dinkspace.feature.openplay.presentation.OpenPlayScreen
import com.thelazybattley.dinkspace.feature.partners.presentation.PartnersScreen
import com.thelazybattley.dinkspace.feature.record.presentation.RecordScreen

@Composable
fun DinkSpaceNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Courts,
        modifier = modifier,
    ) {
        composable<Route.Courts> {
            CourtsRoot(
                onCourtClick = { courtId, date, range ->
                    navController.navigate(Route.CourtDetail(courtId, date.toString(), range.startHour, range.endHour))
                },
            )
        }
        composable<Route.CourtDetail> {
            CourtDetailRoot(onBackClick = { navController.navigateUp() })
        }
        composable<Route.OpenPlay> { OpenPlayScreen() }
        composable<Route.Partners> { PartnersScreen() }
        composable<Route.Record> { RecordScreen() }
    }
}
