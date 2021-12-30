package pl.konarzewski.uboze.ui.main.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.yuyakaido.android.cardstackview.*
import org.joda.time.DateTime
import org.joda.time.DateTimeFieldType
import pl.konarzewski.uboze.R
import pl.konarzewski.uboze.database.AppDatabase
import pl.konarzewski.uboze.model.EngineOne
import pl.konarzewski.uboze.ui.main.adapter.CardStackAdapter

class MainFragment : Fragment() {

    private lateinit var cardStackView: CardStackView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val engineOne = EngineOne(requireContext()!!.applicationContext)
        val currHour = DateTime().get(DateTimeFieldType.hourOfDay())
        val newPathsFromToday = engineOne.getNewPathsFromToday()
        engineOne.initPaths(newPathsFromToday)
        val paths =
            if (currHour > 5 && currHour < 22)
                engineOne.getPathsToLearn()
            else
                engineOne.getPathsToRepeat()
        val ctx = requireContext()!!.applicationContext

        cardStackView = view.findViewById(R.id.card_stack_view)
        val manager = getCardStackManager(ctx, engineOne, paths)
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(LinearInterpolator());
        cardStackView.layoutManager = manager
        cardStackView.adapter = CardStackAdapter(paths)
        cardStackView.itemAnimator = DefaultItemAnimator()
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

    fun getCardStackManager(ctx: Context, e: EngineOne, paths: List<String>): CardStackLayoutManager =
        CardStackLayoutManager(ctx, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {}

            override fun onCardSwiped(direction: Direction?) {}

            override fun onCardRewound() {}

            override fun onCardCanceled() {}

            override fun onCardAppeared(view: View?, position: Int) {}

            override fun onCardDisappeared(view: View?, position: Int) =
                e.repeat(paths[position])
        })
}
