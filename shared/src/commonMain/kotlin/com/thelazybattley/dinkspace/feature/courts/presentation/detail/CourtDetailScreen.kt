package com.thelazybattley.dinkspace.feature.courts.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.thelazybattley.dinkspace.core.presentation.theme.DinkSpaceTheme
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtContact
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtRates
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtSetting
import com.thelazybattley.dinkspace.feature.courts.domain.model.HourSlot
import com.thelazybattley.dinkspace.feature.courts.domain.model.SlotStatus
import com.thelazybattley.dinkspace.feature.courts.presentation.components.DateChips
import com.thelazybattley.dinkspace.feature.courts.presentation.components.TimeRangePicker
import com.thelazybattley.dinkspace.feature.courts.presentation.courtSummary
import com.thelazybattley.dinkspace.feature.courts.presentation.detail.components.CollapsingCourtHeader
import com.thelazybattley.dinkspace.feature.courts.presentation.detail.components.PhotoViewer
import com.thelazybattley.dinkspace.feature.courts.presentation.formatHour
import com.thelazybattley.dinkspace.feature.courts.presentation.formatHourRange
import com.thelazybattley.dinkspace.feature.courts.presentation.formatRate
import com.thelazybattley.dinkspace.feature.courts.presentation.googleMapsUrl
import com.thelazybattley.dinkspace.feature.courts.presentation.shortLabel
import com.thelazybattley.dinkspace.feature.courts.presentation.withoutScheme
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.court_detail_all_open_hours
import dinkspace.shared.generated.resources.court_detail_availability
import dinkspace.shared.generated.resources.court_detail_call
import dinkspace.shared.generated.resources.court_detail_call_to_book
import dinkspace.shared.generated.resources.court_detail_col_free
import dinkspace.shared.generated.resources.court_detail_col_rate
import dinkspace.shared.generated.resources.court_detail_col_status
import dinkspace.shared.generated.resources.court_detail_col_time
import dinkspace.shared.generated.resources.court_detail_free_none
import dinkspace.shared.generated.resources.court_detail_free_of
import dinkspace.shared.generated.resources.court_detail_location
import dinkspace.shared.generated.resources.court_detail_maps_chip
import dinkspace.shared.generated.resources.court_detail_message
import dinkspace.shared.generated.resources.court_detail_messenger
import dinkspace.shared.generated.resources.court_detail_no_rate
import dinkspace.shared.generated.resources.court_detail_not_found
import dinkspace.shared.generated.resources.court_detail_open_in_maps
import dinkspace.shared.generated.resources.court_detail_open_range
import dinkspace.shared.generated.resources.court_detail_phone
import dinkspace.shared.generated.resources.court_detail_photo_count
import dinkspace.shared.generated.resources.court_detail_rates_note
import dinkspace.shared.generated.resources.court_detail_view_photo
import dinkspace.shared.generated.resources.court_setting_indoor_label
import dinkspace.shared.generated.resources.court_setting_outdoor_label
import dinkspace.shared.generated.resources.ic_call
import dinkspace.shared.generated.resources.ic_clock
import dinkspace.shared.generated.resources.ic_court_grid
import dinkspace.shared.generated.resources.ic_courts
import dinkspace.shared.generated.resources.ic_messenger
import dinkspace.shared.generated.resources.price_amount
import dinkspace.shared.generated.resources.slot_booked
import dinkspace.shared.generated.resources.slot_closed
import dinkspace.shared.generated.resources.slot_open
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val MAX_THUMBNAILS = 5

@Composable
fun CourtDetailRoot(
    onBackClick: () -> Unit,
    viewModel: CourtDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    CourtDetailScreen(
        state = state,
        onAction = { action ->
            when (action) {
                CourtDetailAction.OnBackClick -> onBackClick()
                is CourtDetailAction.OnCallClick -> uriHandler.openUri("tel:${action.phoneNumber}")
                is CourtDetailAction.OnMessengerClick -> uriHandler.openUri(action.url.withScheme())
                is CourtDetailAction.OnOpenMapsClick -> uriHandler.openUri(action.url)
                else -> Unit
            }
            viewModel.onAction(action)
        },
    )
}

private fun String.withScheme(): String = if (contains("://")) this else "https://$this"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourtDetailScreen(
    state: CourtDetailState,
    onAction: (CourtDetailAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val court = state.court

    LaunchedEffect(state.error) {
        val error = state.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(getString(error))
        onAction(CourtDetailAction.OnErrorShown)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CollapsingCourtHeader(
                name = court?.name ?: if (state.isLoading) null else "",
                subtitle = court?.let { "${it.area} · ${stringResource(it.setting.label())}" }.orEmpty(),
                logoUrl = court?.logoUrl,
                scrollBehavior = scrollBehavior,
                onBackClick = { onAction(CourtDetailAction.OnBackClick) },
            )
        },
        bottomBar = { court?.let { BookingBar(it.contact, onAction) } },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                state.isLoading -> CourtDetailSkeleton()
                court == null -> Text(
                    text = stringResource(Res.string.court_detail_not_found),
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                else -> CourtDetailContent(court = court, state = state, onAction = onAction)
            }
        }
    }
}

@Composable
private fun CourtDetailContent(
    court: Court,
    state: CourtDetailState,
    onAction: (CourtDetailAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        // Availability sits right under the header so it shows without scrolling.
        QuickFacts(court = court, onAction = onAction, modifier = Modifier.padding(horizontal = 20.dp))
        AvailabilitySection(court = court, state = state, onAction = onAction)
        if (court.photoUrls.isNotEmpty()) {
            CourtPhotos(
                photoUrls = court.photoUrls,
                courtName = court.name,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
        ContactCard(court = court, onAction = onAction, modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@Composable
private fun CourtPhotos(
    photoUrls: List<String>,
    courtName: String,
    modifier: Modifier = Modifier,
) {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var isViewerOpen by rememberSaveable { mutableStateOf(false) }
    val index = selected.coerceIn(0, photoUrls.lastIndex)

    if (isViewerOpen) {
        PhotoViewer(
            photoUrls = photoUrls,
            initialIndex = index,
            contentDescription = courtName,
            onDismiss = { lastIndex ->
                selected = lastIndex
                isViewerOpen = false
            },
        )
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(196.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClickLabel = stringResource(Res.string.court_detail_view_photo)) { isViewerOpen = true },
        ) {
            AsyncImage(
                model = photoUrls[index],
                contentDescription = courtName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            if (photoUrls.size > 1) {
                Text(
                    text = stringResource(Res.string.court_detail_photo_count, index + 1, photoUrls.size),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                )
            }
        }
        if (photoUrls.size > 1) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                photoUrls.take(MAX_THUMBNAILS).forEachIndexed { i, url ->
                    val isSelected = i == index
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = 2.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(10.dp),
                            )
                            .alpha(if (isSelected) 1f else 0.75f)
                            .clickable { selected = i },
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}

/** Number of courts, opening hours and a Maps shortcut, shown above the availability. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickFacts(
    court: Court,
    onAction: (CourtDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        FactChip(icon = Res.drawable.ic_court_grid, text = court.courtSummary())
        FactChip(
            icon = Res.drawable.ic_clock,
            text = stringResource(Res.string.court_detail_open_range, formatHour(court.openingHour), formatHour(court.closingHour)),
        )
        val mapsUrl = court.googleMapsUrl()
        FactChip(
            icon = Res.drawable.ic_courts,
            text = stringResource(Res.string.court_detail_maps_chip),
            outlined = true,
            onClick = { onAction(CourtDetailAction.OnOpenMapsClick(mapsUrl)) },
        )
    }
}

@Composable
private fun FactChip(
    icon: DrawableResource,
    text: String,
    outlined: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .height(32.dp)
            .clip(shape)
            .then(
                if (outlined) Modifier.border(1.dp, colors.outline, shape) else Modifier.background(colors.primaryContainer),
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        val content = if (outlined) colors.primary else colors.onPrimaryContainer
        Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(14.dp), tint = content)
        Text(text = text, style = MaterialTheme.typography.labelLarge, color = content)
    }
}

@Composable
private fun ContactCard(
    court: Court,
    onAction: (CourtDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape),
    ) {
        val mapsUrl = court.googleMapsUrl()
        DetailRow(
            icon = Res.drawable.ic_courts,
            label = stringResource(Res.string.court_detail_location),
            value = court.address,
        ) {
            // Shown under the address.
            Text(
                text = stringResource(Res.string.court_detail_open_in_maps),
                modifier = Modifier.clickable { onAction(CourtDetailAction.OnOpenMapsClick(mapsUrl)) },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        RowDivider()
        DetailRow(
            icon = Res.drawable.ic_call,
            label = stringResource(Res.string.court_detail_phone),
            value = court.contact.phoneNumber,
            trailing = {
                Button(onClick = { onAction(CourtDetailAction.OnCallClick(court.contact.phoneNumber)) }, shape = RoundedCornerShape(10.dp)) {
                    Text(stringResource(Res.string.court_detail_call))
                }
            },
        )
        court.contact.messengerUrl?.let { url ->
            RowDivider()
            DetailRow(
                icon = Res.drawable.ic_messenger,
                label = stringResource(Res.string.court_detail_messenger),
                value = url.withoutScheme(),
                trailing = {
                    OutlinedButton(
                        onClick = { onAction(CourtDetailAction.OnMessengerClick(url)) },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    ) {
                        Text(stringResource(Res.string.court_detail_message))
                    }
                },
            )
        }
    }
}

@Composable
private fun DetailRow(
    icon: DrawableResource,
    label: String,
    value: String,
    trailing: (@Composable RowScope.() -> Unit)? = null,
    below: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            below?.invoke()
        }
        trailing?.invoke(this)
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}

@Composable
private fun AvailabilitySection(
    court: Court,
    state: CourtDetailState,
    onAction: (CourtDetailAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.court_detail_availability),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
            )
            val openRange = court.openRange
            if (openRange != null && openRange != state.timeSelection.range) {
                TextButton(onClick = { onAction(CourtDetailAction.OnShowAllOpenHours) }) {
                    Text(stringResource(Res.string.court_detail_all_open_hours))
                }
            }
        }
        DateChips(
            dates = state.dates,
            selectedDate = state.selectedDate,
            onSelect = { onAction(CourtDetailAction.OnDateSelect(it)) },
        )
        // Narrows the table to the hours the player cares about, starting from the Courts filter.
        TimeRangePicker(
            selection = state.timeSelection,
            selectedDate = state.selectedDate,
            onHourClick = { onAction(CourtDetailAction.OnHourClick(it)) },
        )
        Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val schedule = state.schedule
            if (schedule == null) {
                ScheduleTableSkeleton()
            } else {
                ScheduleTable(court = court, slots = schedule, selectedDate = state.selectedDate)
            }
            Text(
                text = stringResource(Res.string.court_detail_rates_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ScheduleTable(
    court: Court,
    slots: List<HourSlot>,
    selectedDate: LocalDate,
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            HeaderCell(stringResource(Res.string.court_detail_col_time), TIME_WEIGHT)
            HeaderCell(stringResource(Res.string.court_detail_col_free), FREE_WEIGHT)
            HeaderCell(stringResource(Res.string.court_detail_col_rate), RATE_WEIGHT, TextAlign.End)
            HeaderCell(stringResource(Res.string.court_detail_col_status), STATUS_WEIGHT, TextAlign.End)
        }
        slots.forEach { slot ->
            RowDivider()
            ScheduleRow(court, slot, isNextDay = slot.date != selectedDate)
        }
    }
}

@Composable
private fun RowScope.HeaderCell(text: String, weight: Float, align: TextAlign = TextAlign.Start) {
    Text(
        text = text.uppercase(),
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = align,
    )
}

@Composable
private fun ScheduleRow(court: Court, slot: HourSlot, isNextDay: Boolean) {
    val colors = MaterialTheme.colorScheme
    val muted = slot.status != SlotStatus.OPEN
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(TIME_WEIGHT)) {
            Text(text = formatHourRange(slot.startHour, slot.startHour + 1), style = MaterialTheme.typography.labelLarge)
            if (isNextDay) {
                Text(text = slot.date.shortLabel(), style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
            }
        }
        Text(
            text = when (slot.status) {
                SlotStatus.OPEN -> stringResource(Res.string.court_detail_free_of, slot.freeCourts, court.courtCount)
                SlotStatus.BOOKED -> stringResource(Res.string.court_detail_free_none)
                SlotStatus.CLOSED -> stringResource(Res.string.slot_closed)
            },
            modifier = Modifier.weight(FREE_WEIGHT),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
        )
        Text(
            text = court.rates?.let { stringResource(Res.string.price_amount, formatRate(it.rateAt(slot.startHour))) }
                ?: stringResource(Res.string.court_detail_no_rate),
            modifier = Modifier.weight(RATE_WEIGHT),
            style = MaterialTheme.typography.labelLarge,
            color = if (muted) colors.onSurfaceVariant else colors.onSurface,
            textAlign = TextAlign.End,
        )
        Box(modifier = Modifier.weight(STATUS_WEIGHT), contentAlignment = Alignment.CenterEnd) {
            StatusPill(slot.status)
        }
    }
}

@Composable
private fun StatusPill(status: SlotStatus) {
    val colors = MaterialTheme.colorScheme
    val (text, container, content) = when (status) {
        SlotStatus.OPEN -> Triple(stringResource(Res.string.slot_open), colors.primaryContainer, colors.onPrimaryContainer)
        SlotStatus.BOOKED -> Triple(stringResource(Res.string.slot_booked), colors.surfaceVariant, colors.onSurfaceVariant)
        SlotStatus.CLOSED -> Triple(stringResource(Res.string.slot_closed), colors.surfaceVariant, colors.onSurfaceVariant)
    }
    Text(
        text = text,
        modifier = Modifier
            .background(container, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelMedium,
        color = content,
    )
}

@Composable
private fun BookingBar(
    contact: CourtContact,
    onAction: (CourtDetailAction) -> Unit,
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = { onAction(CourtDetailAction.OnCallClick(contact.phoneNumber)) },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                Icon(painterResource(Res.drawable.ic_call), contentDescription = null, modifier = Modifier.size(18.dp))
                Text(stringResource(Res.string.court_detail_call_to_book), modifier = Modifier.padding(start = 8.dp))
            }
            contact.messengerUrl?.let { url ->
                OutlinedButton(
                    onClick = { onAction(CourtDetailAction.OnMessengerClick(url)) },
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_messenger),
                        contentDescription = stringResource(Res.string.court_detail_messenger),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

private fun CourtSetting.label() = when (this) {
    CourtSetting.INDOOR -> Res.string.court_setting_indoor_label
    CourtSetting.OUTDOOR -> Res.string.court_setting_outdoor_label
}

private const val TIME_WEIGHT = 1.35f
private const val FREE_WEIGHT = 1f
private const val RATE_WEIGHT = 0.7f
private const val STATUS_WEIGHT = 0.95f

@Preview
@Composable
private fun CourtDetailScreenPreview() {
    val today = LocalDate(2026, 10, 10)
    DinkSpaceTheme {
        CourtDetailScreen(
            state = CourtDetailState(
                dates = List(7) { today.plus(it, DateTimeUnit.DAY) },
                selectedDate = today,
                isLoading = false,
                court = Court(
                    id = "1",
                    name = "Riverside Park Courts",
                    area = "Westside",
                    address = "120 River Rd, Westside",
                    courtCount = 6,
                    setting = CourtSetting.OUTDOOR,
                    openingHour = 6,
                    closingHour = 22,
                    contact = CourtContact(phoneNumber = "+1 (555) 014-2290", messengerUrl = "https://m.me/riversidepickleball"),
                    logoUrl = null,
                    photoUrls = listOf("a", "b", "c"),
                    rates = CourtRates(standardRate = 10.0, peakRate = 14.0, peakStartHour = 17, peakEndHour = 22),
                ),
                schedule = (16 until 20).map { HourSlot(today, it, if (it == 17) SlotStatus.BOOKED else SlotStatus.OPEN, freeCourts = 3) },
            ),
            onAction = {},
        )
    }
}
