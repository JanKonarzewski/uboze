package pl.konarzewski.uboze.adapter

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.joda.time.DateTime
import pl.konarzewski.uboze.dao.ImigeDao
import pl.konarzewski.uboze.database.AppDatabase
import pl.konarzewski.uboze.entity.Imige
import pl.konarzewski.uboze.ui.main.WordItemFragment
import java.io.File

class WordListAdapter(fragment: Fragment, db: AppDatabase) : FragmentStateAdapter(fragment) {

    private var images: List<Imige>
    private val imigeDao: ImigeDao
    private val currDate: DateTime = DateTime()
    val dateToRepeat = mapOf(
        1 to 1,
        2 to 2,
        3 to 3,
        4 to 7,
        5 to 14
    )

    init {

        val path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Screenshots"
        val directory = File(path)
        imigeDao = db.imigeDao()
        val internalFiles = directory.listFiles()
        val databaseImiges = imigeDao.getAll()
        val except = internalFiles.filter { internalImige ->
            databaseImiges.find { databaseImige ->
                databaseImige.path.equals(internalImige.path)
            } == null
        }
        val intersect = databaseImiges.filter { databaseImige ->
            internalFiles.find { internalImige ->
                internalImige.path.equals(databaseImige.path)
            } != null
        }
        val imigesForToday = getImigesForToday(intersect)
        val exceptSorted = except.sortedByDescending { it.lastModified()}
        val newFiles = exceptSorted.take(100 - imigesForToday.size)
        val newImiges = newFiles.map { file -> Imige(file.path, null, null) }

        images = imigesForToday.plus(newImiges)
    }

    private fun getImigesForToday(imiges: List<Imige>): List<Imige> {
        val shitedCurrDate = currDate.minusHours(6).toLocalDate()

        return imiges.filter { imige ->
            !imige.last_rep_date!!.plusDays(dateToRepeat.get(imige.rep_no)!!).minusHours(6).toLocalDate().isAfter(shitedCurrDate)
        }
    }

    override fun getItemCount(): Int = images.size

    override fun createFragment(position: Int): Fragment {
        val fragment = WordItemFragment()
        val imige = images.get(position)
        imige.rep_no = imige.rep_no?.plus(1) ?: 1
        imige.last_rep_date = DateTime()
        fragment.arguments = Bundle().apply { putString("dir", imige.path) }
        imigeDao.insert(imige)
        return fragment
    }
}
