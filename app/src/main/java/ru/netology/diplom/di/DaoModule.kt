package ru.netology.diplom.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.diplom.data.dao.*
import ru.netology.diplom.data.db.AppDb

@InstallIn(SingletonComponent::class)
@Module
object DaoModule {

    @Provides
    fun providePostDao(db: AppDb): PostDao = db.postDao()

    @Provides
    fun provideEventDao(db: AppDb): EventDao = db.eventDao()

    @Provides
    fun provideJobDao(db: AppDb): JobDao = db.jobDao()

    @Provides
    fun provideUserDao(db: AppDb): UserDao = db.userDao()

    @Provides
    fun providePostRemoteKeyDao(db: AppDb): PostRemoteKeyDao = db.postRemoteKeyDao()

    @Provides
    fun provideEventRemoteKeyDao(db: AppDb): EventRemoteKeyDao = db.eventRemoteKeyDao()
}