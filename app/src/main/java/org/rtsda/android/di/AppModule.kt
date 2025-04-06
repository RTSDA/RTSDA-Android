package org.rtsda.android.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import org.rtsda.android.data.repository.PocketBaseRepository
import org.rtsda.android.data.service.JellyfinService
import org.rtsda.android.data.service.PocketBaseService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGlide(@ApplicationContext context: Context): RequestManager {
        return Glide.with(context)
    }

    @Provides
    @Singleton
    fun providePocketBaseService(okHttpClient: OkHttpClient): PocketBaseService {
        return Retrofit.Builder()
            .baseUrl("https://pocketbase.rockvilletollandsda.church/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PocketBaseService::class.java)
    }

    @Provides
    @Singleton
    fun providePocketBaseRepository(pocketBaseService: PocketBaseService): PocketBaseRepository {
        return PocketBaseRepository(pocketBaseService)
    }

    @Provides
    @Singleton
    fun provideJellyfinService(
        pocketBaseRepository: PocketBaseRepository,
        okHttpClient: OkHttpClient
    ): JellyfinService {
        return JellyfinService(pocketBaseRepository, okHttpClient)
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
} 