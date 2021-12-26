package pl.konarzewski.uboze.ui.main.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import org.joda.time.DateTime
import pl.konarzewski.uboze.R
import pl.konarzewski.uboze.database.AppDatabase
import pl.konarzewski.uboze.engine.Engine
import pl.konarzewski.uboze.engine.Engine.Companion.shiftToDate

class MainFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var sharedPref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        sharedPref =
            activity?.getSharedPreferences("uboze_application_state", Context.MODE_PRIVATE)!!

        val engine = Engine(requireContext()!!.applicationContext)

        viewPager = view.findViewById(R.id.pager)
        val paths = engine.getPaths()

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = paths.size

            override fun createFragment(n: Int): Fragment = WordItemFragment(paths[n])
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position > 0)
                    engine.repeat(paths[position])
                setState(position)
            }
        })

        if (getStateTimestamp().shiftToDate() == DateTime().shiftToDate()) {
            viewPager.setCurrentItem(getState())
        }
    }

    override fun onStart() {
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
        super.onStart()
    }

    override fun onDestroyView() {
        AppDatabase.destroyInstance()
        super.onDestroyView()
    }

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        super.onDestroy()
    }

    fun setState(page: Int) =
        sharedPref!!.edit()
            .putInt("page", page)
            .putLong("timestamp_of_page", DateTime().millis)
            .commit()

    fun getState(): Int =
        sharedPref!!
            .getInt("page", 1)

    fun getStateTimestamp(): DateTime =
        sharedPref!!
            .getLong("timestamp_of_page", 0L)?.let { DateTime(it) }

}
