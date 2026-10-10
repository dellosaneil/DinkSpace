package com.thelazybattley.dinkspace.feature.courts.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.thelazybattley.dinkspace.core.presentation.theme.DinkSpaceTheme
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtContact
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtSetting
import com.thelazybattley.dinkspace.feature.courts.presentation.courtSummary
import com.thelazybattley.dinkspace.feature.courts.presentation.formatHour
import com.thelazybattley.dinkspace.feature.courts.presentation.formatRate
import com.thelazybattley.dinkspace.feature.courts.presentation.withoutScheme
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.action_back
import dinkspace.shared.generated.resources.court_detail_call_to_book
import dinkspace.shared.generated.resources.court_detail_contact
import dinkspace.shared.generated.resources.court_detail_email
import dinkspace.shared.generated.resources.court_detail_hours
import dinkspace.shared.generated.resources.court_detail_messenger
import dinkspace.shared.generated.resources.court_detail_not_found
import dinkspace.shared.generated.resources.court_detail_phone
import dinkspace.shared.generated.resources.court_detail_photos
import dinkspace.shared.generated.resources.court_detail_rate
import dinkspace.shared.generated.resources.ic_back
import dinkspace.shared.generated.resources.ic_call
import dinkspace.shared.generated.resources.ic_email
import dinkspace.shared.generated.resources.ic_messenger
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

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
                is CourtDetailAction.OnEmailClick -> uriHandler.openUri("mailto:${action.email}")
                is CourtDetailAction.OnMessengerClick -> uriHandler.openUri(action.url.withScheme())
            }
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onAction(CourtDetailAction.OnBackClick) }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = stringResource(Res.string.action_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            )
        },
        bottomBar = {
            state.court?.let { court ->
                Surface(shadowElevation = 8.dp) {
                    Button(
                        onClick = { onAction(CourtDetailAction.OnCallClick(court.contact.phoneNumber)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(stringResource(Res.string.court_detail_call_to_book))
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            val court = state.court
            when {
                state.isLoading -> CourtDetailSkeleton()
                court == null -> Text(
                    text = stringResource(Res.string.court_detail_not_found),
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                else -> CourtDetailContent(court = court, onAction = onAction)
            }
        }
    }
}

@Composable
private fun CourtDetailContent(
    court: Court,
    onAction: (CourtDetailAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(start = 20.dp, bottom = 20.dp),
        ) {
            VenueLogo(name = court.name, logoUrl = court.logoUrl)
        }

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = court.name, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = court.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    InfoChip(court.courtSummary())
                    InfoChip(
                        stringResource(
                            Res.string.court_detail_hours,
                            formatHour(court.openingHour),
                            formatHour(court.closingHour),
                        ),
                    )
                    court.hourlyRate?.let { rate ->
                        InfoChip(stringResource(Res.string.court_detail_rate, formatRate(rate)))
                    }
                }
            }

            if (court.photoUrls.isNotEmpty()) {
                PhotoGallery(photoUrls = court.photoUrls)
            }

            ContactSection(contact = court.contact, onAction = onAction)
        }
    }
}

/** Shows the venue logo, falling back to the venue's initials while loading, on error or when there is none. */
@Composable
private fun VenueLogo(
    name: String,
    logoUrl: String?,
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (logoUrl == null) {
            LogoInitials(name)
        } else {
            SubcomposeAsyncImage(
                model = logoUrl,
                contentDescription = name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = { LogoInitials(name) },
                error = { LogoInitials(name) },
            )
        }
    }
}

@Composable
private fun LogoInitials(name: String) {
    val initials = name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = initials,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun InfoChip(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}

@Composable
private fun PhotoGallery(photoUrls: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(Res.string.court_detail_photos))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 20.dp),
        ) {
            items(photoUrls) { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .width(160.dp)
                        .height(110.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

@Composable
private fun ContactSection(
    contact: CourtContact,
    onAction: (CourtDetailAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(Res.string.court_detail_contact))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)),
        ) {
            ContactRow(
                icon = Res.drawable.ic_call,
                label = stringResource(Res.string.court_detail_phone),
                value = contact.phoneNumber,
                onClick = { onAction(CourtDetailAction.OnCallClick(contact.phoneNumber)) },
            )
            contact.messengerUrl?.let { url ->
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                ContactRow(
                    icon = Res.drawable.ic_messenger,
                    label = stringResource(Res.string.court_detail_messenger),
                    value = url.withoutScheme(),
                    onClick = { onAction(CourtDetailAction.OnMessengerClick(url)) },
                )
            }
            contact.email?.let { email ->
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                ContactRow(
                    icon = Res.drawable.ic_email,
                    label = stringResource(Res.string.court_detail_email),
                    value = email,
                    onClick = { onAction(CourtDetailAction.OnEmailClick(email)) },
                )
            }
        }
    }
}

@Composable
private fun ContactRow(
    icon: DrawableResource,
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
}

@Preview
@Composable
private fun CourtDetailScreenPreview() {
    DinkSpaceTheme {
        CourtDetailScreen(
            state = CourtDetailState(
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
                    contact = CourtContact(
                        phoneNumber = "+1 (555) 014-2290",
                        messengerUrl = "m.me/riversidepickleball",
                        email = "bookings@riversidecourts.example",
                    ),
                    logoUrl = null,
                    photoUrls = listOf("a", "b", "c"),
                    hourlyRate = null,
                ),
            ),
            onAction = {},
        )
    }
}
