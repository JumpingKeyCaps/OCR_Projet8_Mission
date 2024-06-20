package com.ocrmission.vitesse.di

import android.content.Context
import androidx.room.Room
import com.ocrmission.vitesse.data.repository.CandidateRepository
import com.ocrmission.vitesse.data.room.AppDatabase
import com.ocrmission.vitesse.data.room.DatabasePrepopulateCallback
import com.ocrmission.vitesse.data.room.dao.CandidateDtoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


/**
 * App module used by Hilt to inject dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context, coroutineScope: CoroutineScope): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "VitesseDB").build()
    }

    @Provides
    fun provideDatabasePrepopulateCallback(coroutineScope: CoroutineScope): DatabasePrepopulateCallback {
        return DatabasePrepopulateCallback(coroutineScope)
    }


    @Provides
    fun provideCandidateDao(appDatabase: AppDatabase): CandidateDtoDao {
        return appDatabase.candidateDtoDao()
    }


    @Provides
    @Singleton
    fun provideCandidateRepository(candidateDtoDao: CandidateDtoDao): CandidateRepository{
        return CandidateRepository(candidateDtoDao)
    }



}