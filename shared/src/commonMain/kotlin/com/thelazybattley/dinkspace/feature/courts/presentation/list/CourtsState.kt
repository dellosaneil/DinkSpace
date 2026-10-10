package com.thelazybattley.dinkspace.feature.courts.presentation.list

import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import org.jetbrains.compose.resources.StringResource

data class CourtsState(
    val query: String = "",
    val courts: List<Court> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: StringResource? = null,
)
