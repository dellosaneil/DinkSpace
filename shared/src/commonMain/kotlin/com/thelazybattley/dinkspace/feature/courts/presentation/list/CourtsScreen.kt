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
import androidx.compose.material3.CircularProgressIndicator
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
import com.thelazybattley.dinkspace.feature.courts.presentation.list.components.CourtAvailabilityCard
import com.thelazybattley.dinkspace.feature.courts.presentation.list.components.CourtCard
import com.thelazybattley.dinkspace.feature.courts.presentation.list.components.DateChips
import com.thelazybattley.dinkspace.feature.courts.presentation.list.components.FilterModeTabs
import com.thelazybattley.dinkspace.feature.courts.presentation.list.components.TimeRangePicker
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.courts_empty
import dinkspace.shared.generated.resources.courts_search_hint
import dinkspace.shared.generated.resources.courts_title
import dinkspace.shared.generated.resources.ic_search
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CourtsRoot(
    onCourtClick: (courtId: String) -> Unit,
    viewModel: CourtsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CourtsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is CourtsAction.OnCourtClick -> onCourtClick(action.courtId)
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
                when (state.filterMode) {
                    CourtFilterMode.DATE_TIME -> AvailabilityList(state, onAction)
                    CourtFilterMode.NAME -> NameResultsList(state, onAction)
                }
            }
        }
    }
}

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
            FilterModeTabs(
                selected = state.filterMode,
                onSelect = { onAction(CourtsAction.OnFilterModeChange(it)) },
            )
        }

        when (state.filterMode) {
            CourtFilterMode.DATE_TIME -> {
                DateChips(
                    dates = state.dates,
                    selectedDate = state.selectedDate,
                    onSelect = { onAction(CourtsAction.OnDateSelect(it)) },
                )
                TimeRangePicker(
                    selection = state.timeSelection,
                    onHourClick = { onAction(CourtsAction.OnHourClick(it)) },
                )
            }
            CourtFilterMode.NAME -> OutlinedTextField(
                value = state.query,
                onValueChange = { onAction(CourtsAction.OnQueryChange(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = { Text(stringResource(Res.string.courts_search_hint)) },
                leadingIcon = { Icon(painterResource(Res.drawable.ic_search), contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            )
        }
    }
}

@Composable
private fun AvailabilityList(
    state: CourtsState,
    onAction: (CourtsAction) -> Unit,
) {
    when {
        state.isLoadingAvailability -> CenteredBox { CircularProgressIndicator() }
        state.availability.isEmpty() -> CenteredBox { EmptyCourts() }
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
private fun NameResultsList(
    state: CourtsState,
    onAction: (CourtsAction) -> Unit,
) {
    when {
        state.isLoading -> CenteredBox { CircularProgressIndicator() }
        state.courts.isEmpty() -> CenteredBox { EmptyCourts() }
        else -> CourtList {
            items(state.courts, key = { it.id }) { court ->
                CourtCard(
                    court = court,
                    onClick = { onAction(CourtsAction.OnCourtClick(court.id)) },
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
private fun EmptyCourts() {
    Text(
        text = stringResource(Res.string.courts_empty),
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
        contact = CourtContact("+1 (555) 014-2290", null, null),
        logoUrl = null,
        photoUrls = emptyList(),
        hourlyRate = null,
    )
    DinkSpaceTheme {
        CourtsScreen(
            state = CourtsState(
                dates = List(7) { today.plus(it, DateTimeUnit.DAY) },
                selectedDate = today,
                isLoading = false,
                isLoadingAvailability = false,
                availability = listOf(
                    CourtAvailability(
                        court = court,
                        slots = listOf(
                            HourSlot(16, SlotStatus.OPEN, freeCourts = 3),
                            HourSlot(17, SlotStatus.BOOKED, freeCourts = 0),
                        ),
                    ),
                ),
            ),
            onAction = {},
        )
    }
}
