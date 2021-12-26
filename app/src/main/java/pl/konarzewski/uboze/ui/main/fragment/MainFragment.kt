package pl.konarzewski.uboze.ui.main.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import org.joda.time.DateTime
import org.joda.time.DateTimeFieldType
import pl.konarzewski.uboze.R
import pl.konarzewski.uboze.database.AppDatabase
import pl.konarzewski.uboze.ui.main.adapter.ScreenSlidePagerAdapter
import pl.konarzewski.uboze.ui.main.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var sharedPref: SharedPreferences
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = MainViewModel(requireContext()!!.applicationContext)

        sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)!!


        viewPager = view.findViewById(R.id.pager)

        viewPager.adapter = ScreenSlidePagerAdapter(this, viewModel)

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position > 0)
                    viewModel.repeat(position - 1)
                setState(position)
            }
        })

        if (getStateTimestamp().minusHours(6).toLocalDate().isEqual(DateTime().minusHours(6).toLocalDate()))
            viewPager.setCurrentItem(getState())
        else
            if (DateTime().get(DateTimeFieldType.hourOfDay()) < 23)
                try {
                    viewPager.setCurrentItem(engine.getPositionOfNewImiges())
                } catch (e:Exception) {
                    val a=e.printStackTrace()
                    val v = 1
                }
            else viewPager.setCurrentItem(0)
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

    fun setState(page: Int) {
        val editor = sharedPref!!.edit()
        editor.putInt("page", page)
        editor.putLong("timestamp_of_page", DateTime().millis)
        editor.commit()
    }

    fun getState(): Int {
        return sharedPref!!.getInt("page", 1)
    }

    fun getStateTimestamp(): DateTime {
        return sharedPref!!.getLong("timestamp_of_page", 0L)?.let { DateTime(it) }
    }
}
