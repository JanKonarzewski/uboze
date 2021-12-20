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
import pl.konarzewski.uboze.engine.Engine

class MainFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var engine: Engine
    private lateinit var sharedPref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)!!

        engine = Engine(requireContext()!!.applicationContext)
        viewPager = view.findViewById(R.id.pager)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = engine.size()

            override fun createFragment(position: Int): Fragment {
                val fragment = WordItemFragment()

                fragment.arguments = Bundle().apply { putString("dir", engine.getImige(position).path) }
                return fragment
            }
        }

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position > 0)
                    engine.updateImige(position - 1)
                setState(position)
            }
        })

        if (getStateTimestamp().minusHours(6).toLocalDate().isEqual(DateTime().minusHours(6).toLocalDate())) {
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
