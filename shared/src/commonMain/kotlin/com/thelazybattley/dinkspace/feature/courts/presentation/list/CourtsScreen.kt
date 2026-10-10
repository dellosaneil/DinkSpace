package com.thelazybattley.dinkspace.feature.courts.presentation.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thelazybattley.dinkspace.core.presentation.theme.DinkSpaceTheme
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtContact
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtSetting
import com.thelazybattley.dinkspace.feature.courts.presentation.courtSummary
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.court_view_details
import dinkspace.shared.generated.resources.courts_empty
import dinkspace.shared.generated.resources.courts_search_hint
import dinkspace.shared.generated.resources.courts_title
import dinkspace.shared.generated.resources.ic_courts
import dinkspace.shared.generated.resources.ic_search
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp),
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

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(CourtsAction.OnRefresh) },
                modifier = Modifier.fillMaxSize(),
            ) {
                when {
                    state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    state.courts.isEmpty() -> EmptyCourts(Modifier.align(Alignment.Center))
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.courts, key = { it.id }) { court ->
                            CourtCard(
                                court = court,
                                onClick = { onAction(CourtsAction.OnCourtClick(court.id)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourtCard(
    court: Court,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(text = court.name, style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_courts),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${court.area} · ${court.courtSummary()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = stringResource(Res.string.court_view_details) + " →",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun EmptyCourts(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(32.dp)) {
        Text(
            text = stringResource(Res.string.courts_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun CourtsScreenPreview() {
    DinkSpaceTheme {
        CourtsScreen(
            state = CourtsState(
                isLoading = false,
                courts = listOf(
                    Court(
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
                    ),
                ),
            ),
            onAction = {},
        )
    }
}
