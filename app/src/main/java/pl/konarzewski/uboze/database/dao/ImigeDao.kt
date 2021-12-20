package pl.konarzewski.uboze.database.dao

import androidx.room.*
import pl.konarzewski.uboze.database.entity.Imige

@Dao
interface ImigeDao {
    @Query("SELECT * FROM Imige")
    fun getAll(): List<Imige>

    @Query("SELECT * FROM Imige WHERE path IN (:ImigeIds)")
    fun loadAllByIds(ImigeIds: IntArray): List<Imige>

    //@Query("SELECT * FROM Imige WHERE first_name LIKE :first AND " +
      //      "last_name LIKE :last LIMIT 1")
    //fun findByName(first: String, last: String): Imige

    @Insert
    fun insertAll(vararg Imiges: Imige)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(Imige: Imige)

    @Delete
    fun delete(Imige: Imige)


}