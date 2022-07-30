package ru.netology.diplom.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.diplom.data.Converters
import ru.netology.diplom.data.dao.*
import ru.netology.diplom.data.entity.*

@Database(
    entities = [
        PostEntity::class,
        EventEntity::class,
        JobEntity::class,
        UserEntity::class,
        PostRemoteKeyEntity::class,
        EventRemoteKeyEntity::class
    ], version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun eventDao(): EventDao
    abstract fun jobDao(): JobDao
    abstract fun userDao(): UserDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
}