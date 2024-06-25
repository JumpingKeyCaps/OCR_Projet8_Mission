package com.ocrmission.vitesse.di

import android.content.Context
import androidx.room.Room
import com.ocrmission.vitesse.data.service.network.interfaces.RetrofitService
import com.ocrmission.vitesse.data.service.network.interfaces.VitesseNetworkService
import com.ocrmission.vitesse.data.service.network.VitesseNetworkServiceImpl
import com.ocrmission.vitesse.data.repository.CandidateRepository
import com.ocrmission.vitesse.data.room.AppDatabase
import com.ocrmission.vitesse.data.room.dao.CandidateDtoDao
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


/**
 * App module used by Hilt to inject dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    //LOCAL DATA BASE (ROOM)
    /**
     * Method to provide the CoroutineScope instance to use.
     * @return a coroutine scope instance ready to use.
     */
    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    /**
     * Method to provide the app database instance to use.
     * @param context the application context.
     * @param coroutineScope the coroutine scope instance for the pre-population callback.
     * @return the app database instance ready to use.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context, coroutineScope: CoroutineScope): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "VitesseDB").build()
    }


    /**
     * Method to provide the CandidateDao instance to use.
     * @param appDatabase the app database instance.
     * @return the candidate dao instance ready to use.
     */
    @Provides
    fun provideCandidateDao(appDatabase: AppDatabase): CandidateDtoDao {
        return appDatabase.candidateDtoDao()
    }

    /**
     * Method to provide the candidate repository instance to use.
     * @param candidateDtoDao the candidate dao instance.
     * @return a candidate repository instance ready to use.
     */
    @Provides
    @Singleton
    fun provideCandidateRepository(candidateDtoDao: CandidateDtoDao): CandidateRepository{
        return CandidateRepository(candidateDtoDao)
    }


    //NETWORK (RETROFIT)
    /**
     * Moshi instance
     * build with KotlinJsonAdapterFactory.
     *
     */
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    /**
     * Method to provide the Retrofit instance to use.
     * @return a retrofit instance ready to use with the good URL and parser.
     */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConfig.CURRENCY_CONVERTER_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }


    /**
     * Method to provide the app network service instance to use.
     * @param retrofit the retrofit instance.
     * @return the app network service instance ready to use.
     */
    @Provides
    @Singleton
    fun provideVitesseNetworkService(retrofit: Retrofit): VitesseNetworkService {
        val retrofitService = retrofit.create(RetrofitService::class.java)
        return VitesseNetworkServiceImpl(retrofitService)
    }

}