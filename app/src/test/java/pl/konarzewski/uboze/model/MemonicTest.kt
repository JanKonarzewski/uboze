package pl.konarzewski.uboze.model

import org.junit.Test
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream

class MemonicTest {

    @Test
    fun getPathsToRepeat() {
        //println("here " + getRatcliffObershelp("abc", "abc"))
    }

    @Test
    fun getPathsToRepeatTest() {
        val path = "src/main/res/raw/pl_full.txt"
        val a = getDictPC(path," ")

        try {
            val fos = FileOutputStream("pl_full")
            val oos = ObjectOutputStream(fos)
            oos.writeObject(a)
            oos.close()
            fos.close()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }

    @Test
    fun getPathsToRepeatTest1() {
        val fullPl = getDictPC( "src/main/res/raw/book_2018/pl/pl_full.txt"," ")
        val fullEn = getDictPC( "src/main/res/raw/book_2018/en/en_full.txt"," ")

        val plExceptEn = fullPl.minus(fullEn)
        compress("pl_full_except_en_full", plExceptEn)
    }

    @Test
    fun getPathsToRepeatTest2() {
        val fullEn = getDictPC( "src/main/res/raw/book_2018/en/en_full.txt"," ")

        compress("en_full", fullEn)
    }

    @Test
    fun imionaMeskie() {
        val dics = getDictPC( "src/main/res/raw/imiona_meskie.csv", ",")

        compress("imiona_meskie", dics)
    }

    @Test
    fun imionaZenskie() {
        val dics = getDictPC( "src/main/res/raw/imiona_zenskie.csv", ",")

        compress("imiona_zenskie", dics)
    }

    @Test
    fun properNames() {
        val dics = getDictPC( "src/main/res/raw/proper_names.csv", ",")

        compress("proper_names", dics)
    }

    @Test
    fun googleTranslate() {
        println(translateText("af","pl", "luge"))
    }
}
