package pl.konarzewski.uboze.ui.main.fragment

import android.os.Bundle
import android.os.Environment
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.yuyakaido.android.cardstackview.*
import org.joda.time.DateTime
import pl.konarzewski.uboze.R
import pl.konarzewski.uboze.database.AppDatabase
import pl.konarzewski.uboze.model.*
import pl.konarzewski.uboze.ui.main.CardStackAdapter
import pl.konarzewski.uboze.ui.main.getConfiguredCardStackManager

class MainFragment : Fragment() {

    private lateinit var cardStackView: CardStackView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctx = requireContext()!!.applicationContext
        val currDateTime = DateTime()
        val db = AppDatabase.getInstance(ctx)
        val dcimScreenshotsPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Screenshots"
        val internalFiles = getInteralFiles(dcimScreenshotsPath)
        val internalImagesFromToday = getInteralImigesFromToday(internalFiles, currDateTime)
        val internalImagesNotFromToday = getInteralImigesNotFromToday(internalFiles, currDateTime)
        val except = exceptByPath(internalImagesFromToday, getDatabaseImiges(db))

        initPaths(except, db)

        val databaseImages = getDatabaseImiges(db)
        val internalImagesCombined = internalImagesFromToday.plus(internalImagesNotFromToday)
        val intersect = intersectByPath(databaseImages, internalImagesCombined)
        val paths = getPathsToRepeat(intersect, currDateTime)

        cardStackView = view.findViewById(R.id.card_stack_view)
        cardStackView.layoutManager = getConfiguredCardStackManager(ctx) { position ->
            repeat(paths[position], db)
        }
        cardStackView
        cardStackView.adapter = CardStackAdapter(paths)
        cardStackView.itemAnimator = DefaultItemAnimator()
    }

    override fun onStart() {
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
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
}
