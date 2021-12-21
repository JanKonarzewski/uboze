package pl.konarzewski.uboze.engine

import android.content.Context
import android.os.Environment
import androidx.room.Room
import org.joda.time.DateTime
import org.joda.time.LocalDate
import pl.konarzewski.uboze.database.dao.ImigeDao
import pl.konarzewski.uboze.database.AppDatabase
import pl.konarzewski.uboze.database.entity.Imige
import java.io.File

class Engine(ctx: Context) {

    private var images: List<Imige>
    private val imigeDao: ImigeDao
    private val shift: Int = 6
    private val shiftedCurrDate: LocalDate = DateTime().minusHours(shift).toLocalDate()
    private val db: AppDatabase
    val dateToRepeat = mapOf(
        1 to 1,
        2 to 2,
        3 to 3,
        4 to 7,
        5 to 14
    )

    init {

        db = AppDatabase.getInstance(ctx)

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
        val imigesToBeRepToday = getImigesToBeRepToday(intersect).sortedByDescending { it.path }
        val imigesRepToday = intersect.filter { imige -> imige.last_rep_date!!.minusHours(shift).toLocalDate().isEqual(shiftedCurrDate) }.sortedByDescending { it.path }
        val newImigesFromToday = except.filter { imige -> DateTime(imige.lastModified()).minusHours(6).toLocalDate().isEqual(shiftedCurrDate) }.map { file -> Imige(file.path, null, null) }
        val newImigesNotFromToday = except.filter { imige -> !DateTime(imige.lastModified()).minusHours(6).toLocalDate().isEqual(shiftedCurrDate) }.map { file -> Imige(file.path, null, null) }
        val newImigesFromTodaySorted = newImigesFromToday.sortedByDescending { it.path }
        val newImigesNotFromTodaySorted = newImigesNotFromToday.sortedByDescending { it.path }
        val newImigesNotFromTodaySortedNLatest =
            newImigesNotFromTodaySorted.take(120 - imigesToBeRepToday.size + imigesRepToday.size + newImigesFromTodaySorted.size)
        val emptyEndingImige = Imige("", null, null)

        images = imigesRepToday.plus(newImigesFromTodaySorted.plus(imigesToBeRepToday.plus(newImigesNotFromTodaySortedNLatest.plus(emptyEndingImige))))
    }

    fun size(): Int {
        return images.size
    }

    fun getImige(position: Int): Imige {
        return images.get(position)
    }

    fun updateImige(position: Int) {
        val imige = images.get(position)

        if (imige.last_rep_date == null ||
            imige.rep_no == null ||
            !imige.last_rep_date!!.minusHours(shift).toLocalDate().isEqual(shiftedCurrDate)
        )
            increment(imige)
    }

    private fun increment(imige: Imige) {
        imige.rep_no = imige.rep_no?.plus(1) ?: 1
        imige.last_rep_date = DateTime()
        imigeDao.insert(imige)
    }

    private fun getImigesToBeRepToday(imiges: List<Imige>): List<Imige> {
        return imiges.filter { imige ->
            isImigeForToday(imige)
        }
    }

    private fun isImigeForToday(imige: Imige): Boolean {
        return !getNextRepDate(imige).isAfter(shiftedCurrDate)
    }

    private fun getNextRepDate(imige: Imige): LocalDate {
        return imige.last_rep_date!!.minusHours(shift).toLocalDate().plusDays(dateToRepeat.get(imige.rep_no)!!)
    }

}
