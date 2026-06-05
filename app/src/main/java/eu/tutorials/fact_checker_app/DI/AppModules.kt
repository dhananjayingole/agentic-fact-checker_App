package eu.tutorials.fact_checker_app.DI

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import eu.tutorials.fact_checker_app.data.api.FactCheckerApiService
import eu.tutorials.fact_checker_app.data.local.FactCheckerDatabase
import eu.tutorials.fact_checker_app.data.local.VerificationDao
import eu.tutorials.fact_checker_app.data.repository.UserPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FactCheckerDatabase =
        Room.databaseBuilder(
            context,
            FactCheckerDatabase::class.java,
            FactCheckerDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideVerificationDao(db: FactCheckerDatabase): VerificationDao =
        db.verificationDao()
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)   // AI responses can take time
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        prefsDataStore: UserPreferencesDataStore
    ): Retrofit {
        // Read the saved API URL (blocking only at startup, in DI graph)
        val baseUrl = runBlocking { prefsDataStore.apiBaseUrl.first() }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): FactCheckerApiService =
        retrofit.create(FactCheckerApiService::class.java)
}