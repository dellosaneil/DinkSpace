package com.thelazybattley.dinkspace.feature.courts.presentation.detail

import com.thelazybattley.dinkspace.feature.courts.domain.model.Court

data class CourtDetailState(
    val court: Court? = null,
    val isLoading: Boolean = true,
)
