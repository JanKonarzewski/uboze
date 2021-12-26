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
import java.security.cert.CertPath

class Engine(ctx: Context) {

    private val shift: Int = 6
    private val shiftedCurrDate: LocalDate = DateTime().minusHours(shift).toLocalDate()
    private val db: AppDatabase = AppDatabase.getInstance(ctx)
    private val imigeDao: ImigeDao = db.imigeDao()

    val dateToRepeat = mapOf(
        1 to 1,
        2 to 2,
        3 to 3,
        4 to 7,
        5 to 14
    )

    fun getPaths(): List<String> {

        val path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Screenshots"
        val directory = File(path)
        val internalFiles = directory.listFiles()
        val databaseImiges = imigeDao.getAll()
        imigeDao.delete(Imige("null", null, null))
        val except = internalFiles.filter { internalImige ->
            databaseImiges.find { databaseImige ->
                databaseImige.path == internalImige.path
            } == null
        }
        val intersect = databaseImiges.filter { databaseImige ->
            internalFiles.find { internalImige ->
                internalImige.path == databaseImige.path
            } != null
        }
        val imigesToBeRepToday = getImigesToBeRepToday(intersect).sortedByDescending { it.path }
        val imigesRepToday =
            intersect.filter { imige -> imige.last_rep_date!!.minusHours(shift).toLocalDate().isEqual(shiftedCurrDate) }
                .sortedByDescending { it.path }
        val newImigesFromToday = except.filter { imige ->
            DateTime(imige.lastModified()).minusHours(6).toLocalDate().isEqual(shiftedCurrDate)
        }.map { file -> Imige(file.path, null, null) }
        val newImigesNotFromToday = except.filter { imige ->
            !DateTime(imige.lastModified()).minusHours(6).toLocalDate().isEqual(shiftedCurrDate)
        }.map { file -> Imige(file.path, null, null) }
        val newImigesFromTodaySorted = newImigesFromToday.sortedByDescending { it.path }
        val newImigesNotFromTodaySorted = newImigesNotFromToday.sortedByDescending { it.path }
        val emptyEndingImige = Imige("null", null, null)

        imigesRepToday.plus(
            newImigesFromTodaySorted.plus(
                imigesToBeRepToday.plus(
                    newImigesNotFromTodaySorted.plus(emptyEndingImige)
                )
            )
        )
            .map { imige -> imige.path }
    }

    private fun getImigesToBeRepToday(imiges: List<Imige>): List<Imige> {
        return imiges.filter { imige ->
            isImigeForToday(imige)
        }
        return imiges
    }

    private fun isImigeForToday(imige: Imige): Boolean {
        return !getNextRepDate(imige).isAfter(shiftedCurrDate)
    }

    private fun getNextRepDate(imige: Imige): LocalDate {
        return imige.last_rep_date!!.minusHours(shift).toLocalDate().plusDays(dateToRepeat.get(imige.rep_no)!!)
    }

    fun size(): Int {
        return images.size
    }

    fun getImige(position: Int): Imige {
        return images.get(position)
    }

    fun repeat(path: String) {

        val imige = imigeDao.findById(path)

        if (imige == null)
            imigeDao.insert(Imige(path, 1, DateTime()))
        else if (!imige.last_rep_date!!.minusHours(6).toLocalDate().isEqual(DateTime().minusHours(6).toLocalDate()))
            imigeDao.increment(imige.path)
    }
}
