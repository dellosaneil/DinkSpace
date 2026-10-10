package com.thelazybattley.dinkspace.di

import com.thelazybattley.dinkspace.core.data.database.DinkSpaceDatabase
import com.thelazybattley.dinkspace.feature.courts.data.remote.CourtRemoteDataSource
import com.thelazybattley.dinkspace.feature.courts.data.remote.FirestoreCourtDataSource
import com.thelazybattley.dinkspace.feature.courts.data.repository.OfflineFirstCourtRepository
import com.thelazybattley.dinkspace.feature.courts.domain.repository.CourtRepository
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.ObserveCourtUseCase
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.RefreshCourtsUseCase
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.SearchCourtsUseCase
import com.thelazybattley.dinkspace.feature.courts.presentation.detail.CourtDetailViewModel
import com.thelazybattley.dinkspace.feature.courts.presentation.list.CourtsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val courtsModule = module {
    single { get<DinkSpaceDatabase>().courtDao() }
    singleOf(::FirestoreCourtDataSource) bind CourtRemoteDataSource::class
    singleOf(::OfflineFirstCourtRepository) bind CourtRepository::class

    factoryOf(::SearchCourtsUseCase)
    factoryOf(::ObserveCourtUseCase)
    factoryOf(::RefreshCourtsUseCase)

    viewModelOf(::CourtsViewModel)
    viewModelOf(::CourtDetailViewModel)
}
