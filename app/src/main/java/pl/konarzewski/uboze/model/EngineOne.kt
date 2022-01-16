package pl.konarzewski.uboze.model

import org.joda.time.DateTime
import org.joda.time.LocalDate
import pl.konarzewski.uboze.database.AppDatabase
import pl.konarzewski.uboze.database.entity.Imige
import pl.konarzewski.uboze.model.EngineOne.Companion.shiftToDate
import java.io.File

val dateToRepeat = mapOf( //0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55,
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

// Retreve
fun getInteralFiles(path: String): Array<File> =
    File(path)
        .listFiles()

fun getInteralImigesFromToday(files: Array<File>, currDate: DateTime): List<Imige> =
    files
        .filter { file -> DateTime(file.lastModified()).shiftToDate() == currDate.shiftToDate() }
        .map { file -> Imige(file.path, null, null) }

fun getInteralImigesNotFromToday(files: Array<File>, currDate: DateTime): List<Imige> =
    files
        .filter { file -> DateTime(file.lastModified()).shiftToDate() != currDate.shiftToDate() }
        .map { file -> Imige(file.path, null, null) }

fun getDatabaseImiges(db: AppDatabase): List<Imige> = db.imigeDao().getAll()

// Operate
fun exceptByPath(a: List<Imige>, b: List<Imige>): List<Imige> =
    a.filter { internalImige ->
        b.find { databaseImige ->
            databaseImige.path == internalImige.path
        } == null
    }.map { imige -> Imige(imige.path, null, null) }

fun intersectByPath(a: List<Imige>, b: List<Imige>): List<Imige> = //filterNotAvailableImiges
    a.filter { databaseImige ->
        b.find { internalImige ->
            internalImige.path == databaseImige.path
        } != null
    }

fun getPathsToRepeat(intersect: List<Imige>, currDate: DateTime): List<Imige> =
    intersect.filter { imige ->
        isImigeForToday(imige, currDate)
    }
        .sortedBy { it.path }

fun isImigeForToday(imige: Imige, date: DateTime): Boolean {
    return !getNextRepDate(imige).isAfter(date.shiftToDate())
}

private fun getNextRepDate(imige: Imige): LocalDate {
    return imige.last_rep_date!!.shiftToDate().plusDays(dateToRepeat[imige.rep_no]!!)
}

// Modify operations on DB
fun initPaths(paths: List<Imige>, db: AppDatabase) =
    paths.forEach { imige -> db.imigeDao().init(imige.path) }

fun repeat(path: String, db: AppDatabase) {
    val imgDao = db.imigeDao()
    val imige = imgDao.findById(path)
    if (imige == null)
        imgDao.init(path)
    else if (isImigeForToday(imige, DateTime()))
        imgDao.increment(path)
}

class EngineOne {

    companion object {
        fun DateTime.shiftToDate(shift: Int = 6): LocalDate =
            this.minusHours(shift).toLocalDate()
    }
}

/*
fun getPathsToLearn(except: List<Imige>): List<Imige> =
    except
        .sortedByDescending { it.path } // .filter{ imige -> tensorFlowIsValid(imige)} - uzyc Tensorflow
 */
