package com.heixss.exchange.di.modules

import android.content.Context
import com.heixss.exchange.network.ExchangeApi
import com.heixss.exchange.network.HeadersInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideExchangeApi(retrofit: Retrofit): ExchangeApi {
        return retrofit.create(ExchangeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://hiring.revolut.codes/")
            // Moshi maps JSON to classes
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache, headersInterceptor: HeadersInterceptor): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.addInterceptor(headersInterceptor)
        client.addInterceptor(
            HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        client.cache(cache)
        client.connectionPool(ConnectionPool(5, 3, TimeUnit.SECONDS))
        return client.build()
    }

    @Singleton
    @Provides
    fun provideOkHttpCache(@ApplicationContext context: Context): Cache {
        return Cache(File(context.cacheDir, "okhttp-cache"), (3 * 1000 * 1000).toLong())
    }
}