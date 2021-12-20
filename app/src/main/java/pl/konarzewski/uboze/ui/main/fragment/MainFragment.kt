package pl.konarzewski.uboze.ui.main.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import pl.konarzewski.uboze.R
import pl.konarzewski.uboze.engine.Engine

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

    override fun onStart() {
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
        super.onStart()
    }
}
