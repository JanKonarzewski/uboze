package pl.konarzewski.uboze.database.dao

import androidx.room.*
import org.joda.time.DateTime
import pl.konarzewski.uboze.database.conventer.DateTimeConverter
import pl.konarzewski.uboze.database.entity.Imige

@Dao
@TypeConverters(DateTimeConverter::class)
interface ImigeDao {

    @Query("SELECT * FROM Imige")
    fun getAll(): List<Imige>

    @Query("SELECT * FROM Imige WHERE path = :path")
    fun findById(path: String): Imige

    @Query("INSERT INTO Imige (path, repetition_number, last_repetition_date) VALUES (:path, 0, :date)")
    fun init(path: String, date: DateTime = DateTime()): Long

    @Query("UPDATE Imige SET repetition_number = repetition_number + 1, last_repetition_date = :date WHERE path = :path")
    fun increment(path: String, date: DateTime = DateTime())

    @Query("SELECT EXISTS(SELECT * FROM Imige WHERE path = :path)")
    fun exist(path: String): Boolean
}
