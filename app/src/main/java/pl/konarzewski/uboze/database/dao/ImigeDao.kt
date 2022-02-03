package pl.konarzewski.uboze.database.dao

import androidx.room.*
import org.joda.time.DateTime
import pl.konarzewski.uboze.database.conventer.DateTimeConverter
import pl.konarzewski.uboze.database.entity.Image

@Dao
@TypeConverters(DateTimeConverter::class)
interface ImigeDao {

    @Query("SELECT * FROM Image")
    fun getAll(): List<Image>

    @Query("SELECT * FROM Image WHERE path = :path")
    fun findById(path: String): Image

    @Query("INSERT INTO Image (path, repetition_number, last_repetition_date) VALUES (:path, 0, :date)")
    fun init(path: String, date: DateTime = DateTime()): Long

    @Query("UPDATE Image SET repetition_number = repetition_number + 1, last_repetition_date = :date WHERE path = :path")
    fun increment(path: String, date: DateTime = DateTime())

    @Query("SELECT EXISTS(SELECT * FROM Image WHERE path = :path)")
    fun exist(path: String): Boolean
}
