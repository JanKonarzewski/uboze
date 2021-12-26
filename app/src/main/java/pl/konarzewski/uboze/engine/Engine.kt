package pl.konarzewski.uboze.engine

import android.content.Context
import android.os.Environment
import org.joda.time.DateTime
import org.joda.time.LocalDate
import pl.konarzewski.uboze.database.dao.ImigeDao
import pl.konarzewski.uboze.database.AppDatabase
import pl.konarzewski.uboze.database.entity.Imige
import java.io.File

class Engine(ctx: Context) {

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

        val shiftedCurrDate = DateTime().shiftToDate()
        val path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Screenshots"
        val directory = File(path)
        val internalFiles = directory.listFiles()
        val databaseImiges = imigeDao.getAll()
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
        val imigesToBeRepToday = getImigesToBeRepToday(intersect, DateTime()).sortedByDescending { it.path }
        val imigesRepToday =
            intersect.filter { imige -> imige.last_rep_date!!.shiftToDate() == shiftedCurrDate }
                .sortedByDescending { it.path }
        val newImigesFromToday = except.filter { imige ->
            (DateTime(imige.lastModified()).minusHours(6).toLocalDate() == shiftedCurrDate)
        }.map { file -> Imige(file.path, null, null) }
        val newImigesNotFromToday = except.filter { imige ->
            !(DateTime(imige.lastModified()).minusHours(6).toLocalDate() == shiftedCurrDate)
        }.map { file -> Imige(file.path, null, null) }
        val newImigesFromTodaySorted = newImigesFromToday.sortedByDescending { it.path }
        val newImigesNotFromTodaySorted = newImigesNotFromToday.sortedByDescending { it.path }
        val emptyEndingImige = Imige("null", null, null)

        return imigesRepToday.plus(
            newImigesFromTodaySorted.plus(
                imigesToBeRepToday.plus(
                    emptyEndingImige
                )
            )
        )
            .map { imige -> imige.path }
    }

    private fun getImigesToBeRepToday(imiges: List<Imige>, date: DateTime): List<Imige> {
        return imiges.filter { imige ->
            isImigeForToday(imige, date)
        }
    }

    private fun isImigeForToday(imige: Imige, date: DateTime): Boolean {
        return !getNextRepDate(imige).isAfter(date.shiftToDate())
    }

    private fun getNextRepDate(imige: Imige): LocalDate {
        return imige.last_rep_date!!.shiftToDate().plusDays(dateToRepeat.get(imige.rep_no)!!)
    }

    fun repeat(path: String) {
        val imige = imigeDao.findById(path)

        if (imige == null)
            imigeDao.init(path)
        else if (!(imige.last_rep_date!!.shiftToDate() == DateTime().shiftToDate()))
            imigeDao.increment(path)
    }

    companion object {
        fun DateTime.shiftToDate(shift: Int = 6): LocalDate =
            this.minusHours(shift).toLocalDate()
    }
}
