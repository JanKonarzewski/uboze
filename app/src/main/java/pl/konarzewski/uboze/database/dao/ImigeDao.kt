package pl.konarzewski.uboze.database.dao

import androidx.room.*
import org.joda.time.DateTime
import pl.konarzewski.uboze.database.conventer.DateTimeConverter
import pl.konarzewski.uboze.database.entity.Imige

@Dao
@TypeConverters(DateTimeConverter::class)
interface ImigeDao( ) {
    @Query("SELECT * FROM Imige")
    fun getAll(): List<Imige>

    @Query("SELECT * FROM Imige WHERE path IN (:ImigeIds)")
    fun loadAllByIds(ImigeIds: IntArray): List<Imige>

    @Insert
    fun insertAll(vararg Imiges: Imige)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(Imige: Imige)

    @Delete
    fun delete(Imige: Imige)

    @Query("SELECT * FROM Imige WHERE path = :path")
    fun findById(path: String): Imige

    @Query("UPDATE Imige SET repetition_number = repetition_number + 1, last_repetition_date = :date WHERE path = :path")
    fun increment(path: String, date: DateTime = DateTime())
}