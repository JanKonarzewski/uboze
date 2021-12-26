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

    @Query("UPDATE Imige SET repetition_number = repetition_number + 1, last_repetition_date = :date WHERE path = :path")
    fun increment(path: String, date: DateTime = DateTime())

    @Query("INSERT INTO Imige (path, repetition_number, last_repetition_date) VALUES (:path, 1, :date)")
    fun init(path: String, date: DateTime = DateTime())
}