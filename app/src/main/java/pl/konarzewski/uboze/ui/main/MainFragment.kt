package pl.konarzewski.uboze.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import pl.konarzewski.uboze.R

class MainFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var engine: Engine

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        engine = Engine(requireContext()!!.applicationContext)

        viewPager = view.findViewById(R.id.pager)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = engine.size()

            override fun createFragment(position: Int): Fragment {

                val fragment = WordItemFragment()

                val i = engine.getImige(position).path
                fragment.arguments = Bundle().apply { putString("dir", i) }
                return fragment
            }
        }
    }
}
