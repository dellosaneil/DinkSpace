package com.thelazybattley.dinkspace.feature.courts.presentation.list

sealed interface CourtsAction {
    data class OnQueryChange(val query: String) : CourtsAction
    data class OnCourtClick(val courtId: String) : CourtsAction
    data object OnRefresh : CourtsAction
    data object OnErrorShown : CourtsAction
}
