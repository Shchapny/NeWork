package ru.netology.diplom.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.diplom.repository.auth.AuthRepository
import ru.netology.diplom.repository.auth.AuthRepositoryImpl
import ru.netology.diplom.repository.event.EventRepository
import ru.netology.diplom.repository.event.EventRepositoryImpl
import ru.netology.diplom.repository.job.JobRepository
import ru.netology.diplom.repository.job.JobRepositoryImpl
import ru.netology.diplom.repository.post.PostRepository
import ru.netology.diplom.repository.post.PostRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindPostRepository(impl: PostRepositoryImpl): PostRepository

    @Singleton
    @Binds
    fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Singleton
    @Binds
    fun bindJobRepository(impl: JobRepositoryImpl): JobRepository

    @Singleton
    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}