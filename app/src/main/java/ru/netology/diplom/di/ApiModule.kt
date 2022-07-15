package ru.netology.diplom.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.diplom.api.*
import ru.netology.diplom.authorization.AppAuth
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

    @Singleton
    @Provides
    fun providePostApi(appAuth: AppAuth): PostApiService {
        return retrofit(okHttpClient(loggingInterceptor(), authInterceptor(appAuth)))
            .create(PostApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideEventApi(appAuth: AppAuth): EventApiService {
        return retrofit(okHttpClient(loggingInterceptor(), authInterceptor(appAuth)))
            .create(EventApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideJobApi(appAuth: AppAuth): JobApiService {
        return retrofit(okHttpClient(loggingInterceptor(), authInterceptor(appAuth)))
            .create(JobApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthApi(appAuth: AppAuth): AuthApiService {
        return retrofit(okHttpClient(loggingInterceptor(), authInterceptor(appAuth)))
            .create(AuthApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideMediaApi(appAuth: AppAuth): MediaApiService {
        return retrofit(okHttpClient(loggingInterceptor(), authInterceptor(appAuth)))
            .create(MediaApiService::class.java)
    }
}