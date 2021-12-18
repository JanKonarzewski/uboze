package pl.konarzewski.uboze.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.konarzewski.uboze.dao.ImigeDao
import pl.konarzewski.uboze.entity.Imige

@Database(entities = [Imige::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imigeDao(): ImigeDao
}