package com.thelazybattley.dinkspace.feature.courts.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thelazybattley.dinkspace.core.presentation.theme.DinkSpaceTheme
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtAvailability
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtContact
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtSetting
import com.thelazybattley.dinkspace.feature.courts.domain.model.HourSlot
import com.thelazybattley.dinkspace.feature.courts.domain.model.SlotStatus
import com.thelazybattley.dinkspace.feature.courts.domain.model.TimeRange
import com.thelazybattley.dinkspace.feature.courts.presentation.components.DateChips
import com.thelazybattley.dinkspace.feature.courts.presentation.components.TimeRangePicker
import com.thelazybattley.dinkspace.feature.courts.presentation.formatHourRange
import com.thelazybattley.dinkspace.feature.courts.presentation.list.components.CourtAvailabilityCard
import com.thelazybattley.dinkspace.feature.courts.presentation.list.components.CourtListSkeleton
import com.thelazybattley.dinkspace.feature.courts.presentation.shortLabel
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.courts_count
import dinkspace.shared.generated.resources.courts_empty
import dinkspace.shared.generated.resources.courts_empty_query
import dinkspace.shared.generated.resources.courts_search_hint
import dinkspace.shared.generated.resources.courts_summary
import dinkspace.shared.generated.resources.courts_summary_query
import dinkspace.shared.generated.resources.courts_title
import dinkspace.shared.generated.resources.date_today
import dinkspace.shared.generated.resources.ic_search
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CourtsRoot(
    onCourtClick: (courtId: String, date: LocalDate, range: TimeRange) -> Unit,
    viewModel: CourtsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CourtsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is CourtsAction.OnCourtClick -> onCourtClick(action.courtId, state.selectedDate, state.timeSelection.range)
                else -> Unit
            }
            viewModel.onAction(action)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourtsScreen(
    state: CourtsState,
    onAction: (CourtsAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        val error = state.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(getString(error))
        onAction(CourtsAction.OnErrorShown)
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            CourtsHeader(state = state, onAction = onAction)

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(CourtsAction.OnRefresh) },
                modifier = Modifier.fillMaxSize(),
            ) {
                CourtResults(state, onAction)
            }
        }
    }
}

/** Name search, day and time range all apply together; the search is optional. */
@Composable
private fun CourtsHeader(
    state: CourtsState,
    onAction: (CourtsAction) -> Unit,
) {
    Column(
        modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.courts_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            OutlinedTextField(
                value = state.query,
                onValueChange = { onAction(CourtsAction.OnQueryChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.courts_search_hint)) },
                leadingIcon = { Icon(painterResource(Res.drawable.ic_search), contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            )
        }
        DateChips(
            dates = state.dates,
            selectedDate = state.selectedDate,
            onSelect = { onAction(CourtsAction.OnDateSelect(it)) },
        )
        TimeRangePicker(
            selection = state.timeSelection,
            selectedDate = state.selectedDate,
            onHourClick = { onAction(CourtsAction.OnHourClick(it)) },
        )
        if (!state.isLoadingAvailability) {
            ResultsSummary(state, Modifier.padding(horizontal = 20.dp))
        }
    }
}

/** "2 courts · Today, 4–6 PM · “riv”" */
@Composable
private fun ResultsSummary(state: CourtsState, modifier: Modifier = Modifier) {
    val count = state.availability.size
    val courts = pluralStringResource(Res.plurals.courts_count, count, count)
    val day = if (state.selectedDate == state.dates.firstOrNull()) {
        stringResource(Res.string.date_today)
    } else {
        state.selectedDate.shortLabel()
    }
    val range = state.timeSelection.range
    val hours = formatHourRange(range.startHour, range.endHour)
    val query = state.query.trim()
    Text(
        text = if (query.isEmpty()) {
            stringResource(Res.string.courts_summary, courts, day, hours)
        } else {
            stringResource(Res.string.courts_summary_query, courts, day, hours, query)
        },
        modifier = modifier,
        style = MaterialTheme.typography.labelLarge,
    )
}

@Composable
private fun CourtResults(
    state: CourtsState,
    onAction: (CourtsAction) -> Unit,
) {
    when {
        state.isLoadingAvailability -> CourtListSkeleton()
        state.availability.isEmpty() -> CenteredBox { EmptyCourts(state.query.trim()) }
        else -> CourtList {
            items(state.availability, key = { it.court.id }) { availability ->
                CourtAvailabilityCard(
                    availability = availability,
                    onClick = { onAction(CourtsAction.OnCourtClick(availability.court.id)) },
                )
            }
        }
    }
}

@Composable
private fun CourtList(content: LazyListScope.() -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

@Composable
private fun EmptyCourts(query: String) {
    Text(
        text = if (query.isEmpty()) {
            stringResource(Res.string.courts_empty)
        } else {
            stringResource(Res.string.courts_empty_query, query)
        },
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(32.dp),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Preview
@Composable
private fun CourtsScreenPreview() {
    val today = LocalDate(2026, 10, 10)
    val court = Court(
        id = "1",
        name = "Riverside Park Courts",
        area = "Westside",
        address = "120 River Rd, Westside",
        courtCount = 6,
        setting = CourtSetting.OUTDOOR,
        openingHour = 6,
        closingHour = 22,
        contact = CourtContact(phoneNumber = "+1 (555) 014-2290", messengerUrl = null),
        logoUrl = null,
        photoUrls = emptyList(),
        rates = null,
    )
    DinkSpaceTheme {
        CourtsScreen(
            state = CourtsState(
                dates = List(7) { today.plus(it, DateTimeUnit.DAY) },
                selectedDate = today,
                isLoadingAvailability = false,
                availability = listOf(
                    CourtAvailability(
                        court = court,
                        slots = listOf(
                            HourSlot(today, 16, SlotStatus.OPEN, freeCourts = 3),
                            HourSlot(today, 17, SlotStatus.BOOKED, freeCourts = 0),
                        ),
                    ),
                ),
            ),
            onAction = {},
        )
    }
}
