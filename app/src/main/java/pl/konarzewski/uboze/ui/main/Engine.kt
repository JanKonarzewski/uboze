package pl.konarzewski.uboze.ui.main

import android.content.Context
import android.os.Environment
import androidx.room.Room
import org.joda.time.DateTime
import pl.konarzewski.uboze.dao.ImigeDao
import pl.konarzewski.uboze.database.AppDatabase
import pl.konarzewski.uboze.entity.Imige
import java.io.File

class Engine(ctx: Context) {

    private var images: List<Imige>
    private val imigeDao: ImigeDao
    private val currDate: DateTime = DateTime()
    private val db: AppDatabase
    val dateToRepeat = mapOf(
        1 to 1,
        2 to 2,
        3 to 3,
        4 to 7,
        5 to 14
    )

    init {

        db = Room.databaseBuilder(
            ctx,
            AppDatabase::class.java,
            "uboze_db_25.db" //uboze-db.db uboze_db_1.db
        ).allowMainThreadQueries().build()

        val path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Screenshots"
        val directory = File(path)
        imigeDao = db.imigeDao()
        val internalFiles = directory.listFiles()
        val databaseImiges = imigeDao.getAll()
        val except = internalFiles.filter { internalImige ->
            databaseImiges.find { databaseImige ->
                databaseImige.path == internalImige.path
            } == null
        }
        val intersect = databaseImiges.filter { databaseImige ->
            internalFiles.find { internalImige ->
                internalImige.path.equals(databaseImige.path)
            } != null
        }
        val imigesForToday = getImigesForToday(intersect)
        val exceptSorted = except.sortedByDescending { it.lastModified() }
        val newFiles = exceptSorted.take(100 - imigesForToday.size)
        val newImiges = newFiles.map { file -> Imige(file.path, null, null) }

        images = imigesForToday.plus(newImiges).plus(Imige("", null, null))
    }

    fun size(): Int {
        return images.size
    }

    fun getImige(position: Int): Imige {
        if (position > 1) {
            val prevImige = images.get(position - 2)
            if (isImigeForToday(prevImige))
                updateImige(prevImige)
        }
        return images.get(position)
    }

    fun updateImige(imige: Imige) {
        imige.rep_no = imige.rep_no?.plus(1) ?: 1
        imige.last_rep_date = DateTime()
        imigeDao.insert(imige)
    }

    private fun getImigesForToday(imiges: List<Imige>): List<Imige> {

        return imiges.filter { imige ->
            isImigeForToday(imige)
        }
    }

    private fun isImigeForToday(imige: Imige): Boolean {
        val shitedCurrDate = currDate.minusHours(6).toLocalDate()
        if(imige.rep_no != null && imige.last_rep_date != null)
            return !imige.last_rep_date!!.plusDays(dateToRepeat.get(imige.rep_no)!!).minusHours(6).toLocalDate()
                .isAfter(shitedCurrDate)
        else
            return true
    }
}
