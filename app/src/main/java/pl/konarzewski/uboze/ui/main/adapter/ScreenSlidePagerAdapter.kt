package pl.konarzewski.uboze.ui.main.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import pl.konarzewski.uboze.ui.main.fragment.WordItemFragment
import pl.konarzewski.uboze.ui.main.viewmodel.MainViewModel

class ScreenSlidePagerAdapter(fa: FragmentActivity, viewModel: MainViewModel) : FragmentStateAdapter(fa) {

    private val paths: List<String> = viewModel.getPaths()

    override fun getItemCount(): Int = paths.size

    override fun createFragment(position: Int): Fragment {
        val fragment = WordItemFragment()
        fragment.arguments = Bundle().apply { putString("dir", paths.get(position)) }
        return fragment
    }
}