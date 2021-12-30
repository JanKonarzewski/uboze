package pl.konarzewski.uboze.model

import android.content.Context
import android.os.Environment
import org.joda.time.DateTime
import org.joda.time.LocalDate
import pl.konarzewski.uboze.database.dao.ImigeDao
import pl.konarzewski.uboze.database.AppDatabase
import pl.konarzewski.uboze.database.entity.Imige
import java.io.File

class EngineOne(ctx: Context) {

    private val db: AppDatabase = AppDatabase.getInstance(ctx)
    private val imageDao: ImigeDao = db.imigeDao()
    private val pathToDcimScreenshots = Environment.getExternalStorageDirectory().toString() + "/DCIM/Screenshots"
    private val dateToRepeat = mapOf( //0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55,
        0 to 0,     //1
        1 to 1,     //2
        2 to 1,     //3
        3 to 2,     //5
        4 to 3,     //8
        5 to 5,     //13
        6 to 8,     //21
        7 to 13,    //34
        8 to 21     //55
    )

    fun initPaths(paths: List<String>) {
        paths.forEach {path -> imageDao.init(path)}
    }

    fun intersect(databaseImiges: List<Imige>, internalFiles: Array<File>): List<Imige> =
        databaseImiges.filter {
                databaseImige -> internalFiles.find {
                    internalImige -> internalImige.path == databaseImige.path
                } != null
        }

    fun except(databaseImiges: List<Imige>, internalFiles: Array<File>): List<Imige> =
        internalFiles.filter {
                internalImige -> databaseImiges.find {
                    databaseImige -> databaseImige.path == internalImige.path
                } == null
        }.map { imige -> Imige(imige.path, null, null) }

    fun getNewPathsFromToday(): List<String> {
        val shiftedCurrDate = DateTime().shiftToDate()
        val dcimScreenshotFile = File(pathToDcimScreenshots)
        val internalFiles = dcimScreenshotFile.listFiles()
        val databaseImiges = imageDao.getAll()

        val except = internalFiles.filter { internalImige -> databaseImiges.find { databaseImige -> databaseImige.path == internalImige.path } == null }

        return except.filter { image -> (DateTime(image.lastModified()).shiftToDate() == shiftedCurrDate) }
            .sortedByDescending { imige -> imige.lastModified() }
            .map { imige -> imige.path }
    }

    fun getPathsToRepeat(): List<String> {
        val dcimScreenshotFile = File(pathToDcimScreenshots)
        val internalFiles = dcimScreenshotFile.listFiles()
        val databaseImiges = imageDao.getAll()

        val intersect = databaseImiges.filter { databaseImige -> internalFiles.find { internalImige -> internalImige.path == databaseImige.path } != null }
        return getImigesToBeRepToday(intersect, DateTime())
            .sortedBy { it.last_rep_date }
            .map { imige -> imige.path }
    }

    fun getPathsToLearn(): List<String> {
        val shiftedCurrDate = DateTime().shiftToDate()
        val dcimScreenshotFile = File(pathToDcimScreenshots)
        val internalFiles = dcimScreenshotFile.listFiles()
        val databaseImiges = imageDao.getAll()

        val except = internalFiles.filter { internalImige -> databaseImiges.find { databaseImige -> databaseImige.path == internalImige.path } == null }

        return except.filter { image -> DateTime(image.lastModified()).shiftToDate() != shiftedCurrDate }
            .sortedByDescending { it.lastModified() }
            .map { imige -> imige.path } // uzyc Tensorflow
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
        return imige.last_rep_date!!.shiftToDate().plusDays(dateToRepeat[imige.rep_no]!!)
    }

    fun repeat(path: String) {
        val imige = imageDao.findById(path)
        if (imige == null)
            imageDao.init(path)
        else if (isImigeForToday(imige, DateTime()))
            imageDao.increment(path)
    }

    companion object {
        fun DateTime.shiftToDate(shift: Int = 6): LocalDate =
            this.minusHours(shift).toLocalDate()
    }
}
