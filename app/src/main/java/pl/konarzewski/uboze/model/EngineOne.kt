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
        .map { file -> Image(file.path, null, null, file.lastModified(), false) }

fun getInteralImigesFromDate(images: List<Image>, date: DateTime): List<Image> =
    images
        .filter { image -> DateTime(image.last_modified).shiftToDate() == date.shiftToDate() }

fun getInteralImigesBeginningFromDate(images: List<Image>, date: DateTime): List<Image> =
    images
        .filter { image -> DateTime(image.last_modified).shiftToDate() >= date.shiftToDate() }

fun getInteralImigesNotFromToday(images: List<Image>, currDate: DateTime): List<Image> =
    images
        .filter { image -> DateTime(image.last_modified).shiftToDate() != currDate.shiftToDate() }

fun getDatabaseImages(db: AppDatabase): List<Image> = db.imigeDao().getAll()

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
    intersect.filter { image ->
        image.isActive && isImigeForToday(image, currDate)
    }.sortedBy { it.last_modified }

fun isImigeForToday(image: Image, date: DateTime): Boolean {
    return !getNextRepDate(image).isAfter(date.shiftToDate())
}

private fun getNextRepDate(image: Image): LocalDate {
    return image.last_rep_date!!.shiftToDate().plusDays(dateToRepeat[image.rep_no]!!)
}

// Modify operations on DB
fun initPaths(image: List<Image>, db: AppDatabase) =
    image.forEach { image -> db.imigeDao().init(image.path, image.last_modified, true) }

fun repeat(image: Image, db: AppDatabase) {
    val imgDao = db.imigeDao()
    val imageDb = imgDao.findById(image.path)
    if (imageDb == null)
        imgDao.init(image.path, image.last_modified, true)
    else if (isImigeForToday(imageDb, DateTime()))
        imgDao.increment(imageDb.path)
}

fun disactivate(image: Image, db: AppDatabase) =
    db.imigeDao().disactivate(image.path)

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
