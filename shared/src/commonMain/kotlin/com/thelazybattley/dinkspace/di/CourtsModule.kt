package com.thelazybattley.dinkspace.di

import com.thelazybattley.dinkspace.core.config.AppConfig
import com.thelazybattley.dinkspace.core.data.database.DinkSpaceDatabase
import com.thelazybattley.dinkspace.feature.courts.data.remote.AvailabilityRemoteDataSource
import com.thelazybattley.dinkspace.feature.courts.data.remote.CourtRemoteDataSource
import com.thelazybattley.dinkspace.feature.courts.data.remote.FakeAvailabilityRemoteDataSource
import com.thelazybattley.dinkspace.feature.courts.data.remote.FakeCourtRemoteDataSource
import com.thelazybattley.dinkspace.feature.courts.data.remote.FirestoreAvailabilityDataSource
import com.thelazybattley.dinkspace.feature.courts.data.remote.FirestoreCourtDataSource
import com.thelazybattley.dinkspace.feature.courts.data.repository.InMemoryAvailabilityRepository
import com.thelazybattley.dinkspace.feature.courts.data.repository.OfflineFirstCourtRepository
import com.thelazybattley.dinkspace.feature.courts.domain.repository.AvailabilityRepository
import com.thelazybattley.dinkspace.feature.courts.domain.repository.CourtRepository
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.ObserveCourtAvailabilityUseCase
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.ObserveCourtScheduleUseCase
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.RefreshAvailabilityUseCase
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
    single<CourtRemoteDataSource> {
        if (AppConfig.USE_FAKE_DATA) FakeCourtRemoteDataSource() else FirestoreCourtDataSource(get())
    }
    singleOf(::OfflineFirstCourtRepository) bind CourtRepository::class
    single<AvailabilityRemoteDataSource> {
        if (AppConfig.USE_FAKE_DATA) FakeAvailabilityRemoteDataSource() else FirestoreAvailabilityDataSource(get())
    }
    singleOf(::InMemoryAvailabilityRepository) bind AvailabilityRepository::class

    factoryOf(::SearchCourtsUseCase)
    factoryOf(::RefreshCourtsUseCase)
    factoryOf(::ObserveCourtAvailabilityUseCase)
    factoryOf(::RefreshAvailabilityUseCase)
    factoryOf(::ObserveCourtScheduleUseCase)

    viewModelOf(::CourtsViewModel)
    viewModelOf(::CourtDetailViewModel)
}
