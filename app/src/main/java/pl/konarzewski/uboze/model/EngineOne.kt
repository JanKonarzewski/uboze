package pl.konarzewski.uboze.model

import org.joda.time.DateTime
import org.joda.time.LocalDate
import pl.konarzewski.uboze.database.AppDatabase
import pl.konarzewski.uboze.database.entity.Image
import pl.konarzewski.uboze.model.EngineOne.Companion.shiftToDate
import java.io.File

val dateToRepeat = mapOf( //0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55,89
    0 to 0,     //1
    1 to 1,     //2
    2 to 1,     //3
    3 to 2,     //5
    4 to 3,     //8
    5 to 5,     //13
    6 to 8,     //21
    7 to 13,    //34
    8 to 21,    //55
    9 to 34,    //89
    10 to 55,   //144
    11 to 89    //233
)

// Retreve
fun getInteralImiges(path: String): List<Image> =
    File(path)
        .listFiles()
        .map { file -> Image(file.path, null, null, file.lastModified()) }

fun getInteralImigesFromToday(images: List<Image>, currDate: DateTime): List<Image> =
    images
        .filter { image -> DateTime(image.last_modified).shiftToDate() == currDate.shiftToDate() }

fun getInteralImigesNotFromToday(images: List<Image>, currDate: DateTime): List<Image> =
    images
        .filter { image -> DateTime(image.last_modified).shiftToDate() != currDate.shiftToDate() }

fun getDatabaseImiges(db: AppDatabase): List<Image> = db.imigeDao().getAll()

// Operate
fun exceptByPath(a: List<Image>, b: List<Image>): List<Image> =
    a.filter { internalImige ->
        b.find { databaseImige ->
            databaseImige.path == internalImige.path
        } == null
    }

fun intersectByPath(a: List<Image>, b: List<Image>): List<Image> = //filterNotAvailableImiges
    a.filter { databaseImage ->
        b.find { internalImage ->
            internalImage.path == databaseImage.path
        } != null
    }


fun getPathsToRepeat(intersect: List<Image>, currDate: DateTime): List<Image> =
    intersect.filter { imige ->
        isImigeForToday(imige, currDate)
    }.sortedBy { it.last_modified }

fun isImigeForToday(image: Image, date: DateTime): Boolean {
    return !getNextRepDate(image).isAfter(date.shiftToDate())
}

private fun getNextRepDate(image: Image): LocalDate {
    return image.last_rep_date!!.shiftToDate().plusDays(dateToRepeat[image.rep_no]!!)
}

// Modify operations on DB
fun initPaths(paths: List<Image>, db: AppDatabase) =
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
