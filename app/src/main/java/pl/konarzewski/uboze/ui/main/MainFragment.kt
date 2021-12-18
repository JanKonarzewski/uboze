package pl.konarzewski.uboze.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import pl.konarzewski.uboze.R
import pl.konarzewski.uboze.adapter.WordListAdapter
import pl.konarzewski.uboze.database.AppDatabase


class MainFragment : Fragment() {

    private lateinit var wordListAdapter: WordListAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = Room.databaseBuilder(
            requireContext()!!.applicationContext,
            AppDatabase::class.java,
            "uboze_db_2.db" //uboze-db.db uboze_db_1.db
        ).allowMainThreadQueries().build()

        wordListAdapter = WordListAdapter(this, db!!)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = wordListAdapter
    }
}
