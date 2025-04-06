package org.rtsda.android.di

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import androidx.media3.datasource.DefaultHttpDataSource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.rtsda.android.api.ConfigApi
import org.rtsda.android.api.JellyfinApi
import org.rtsda.android.api.OwnCastApi
import org.rtsda.android.data.remote.PocketBaseApi
import org.rtsda.android.data.service.BibleService
import org.rtsda.android.data.service.EventService
import org.rtsda.android.data.remote.ContactApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val POCKETBASE_URL = "https://pocketbase.rockvilletollandsda.church/"
    private const val JELLYFIN_URL = "https://jellyfin.rockvilletollandsda.church/"
    private const val OWNCAST_URL = "https://stream.rockvilletollandsda.church/"
    private const val BASE_URL = "https://pocketbase.rockvilletollandsda.church/"

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("rtsda_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("baseUrl")
    fun provideBaseUrl(): String {
        return "https://api.rtsda.org/"
    }

    @Provides
    @Singleton
    @Named("pocketbase")
    fun providePocketBaseRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(POCKETBASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideConfigApi(@Named("pocketbase") retrofit: Retrofit): ConfigApi {
        return retrofit.create(ConfigApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHttpDataSourceFactory(): DefaultHttpDataSource.Factory {
        return DefaultHttpDataSource.Factory()
    }

    @Provides
    @Singleton
    @Named("jellyfin")
    fun provideJellyfinRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(JELLYFIN_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("owncast")
    fun provideOwnCastRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(OWNCAST_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun providePocketBaseApi(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): PocketBaseApi {
        return Retrofit.Builder()
            .baseUrl(POCKETBASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(PocketBaseApi::class.java)
    }

    @Provides
    @Singleton
    fun provideJellyfinApi(@Named("jellyfin") retrofit: Retrofit): JellyfinApi {
        return retrofit.create(JellyfinApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOwnCastApi(@Named("owncast") retrofit: Retrofit): OwnCastApi {
        return retrofit.create(OwnCastApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBibleService(@Named("pocketbase") retrofit: Retrofit): BibleService {
        return retrofit.create(BibleService::class.java)
    }

    @Provides
    @Singleton
    fun provideEventService(@Named("pocketbase") retrofit: Retrofit): EventService {
        return retrofit.create(EventService::class.java)
    }

    @Provides
    @Named("pocketbase_url")
    fun providePocketBaseUrl(): String {
        return POCKETBASE_URL
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://contact.rockvilletollandsda.church/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideContactApi(retrofit: Retrofit): ContactApi {
        return retrofit.create(ContactApi::class.java)
    }
} 