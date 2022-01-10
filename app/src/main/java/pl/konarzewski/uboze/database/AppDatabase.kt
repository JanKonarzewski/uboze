package pl.konarzewski.uboze.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pl.konarzewski.uboze.database.dao.ImigeDao
import pl.konarzewski.uboze.database.entity.Imige

@Database(entities = [Imige::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imigeDao(): ImigeDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(ctx: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(ctx).also {
                    INSTANCE = it
                }
            }

        private fun buildDatabase(ctx: Context) =
            Room.databaseBuilder(
                ctx,
                AppDatabase::class.java,
                "uboze_application_database.db"
            ).allowMainThreadQueries().build()

        fun destroyInstance() {
            if (INSTANCE?.isOpen == true) {
                INSTANCE?.close()
            }

            INSTANCE = null
        }
    }
}
