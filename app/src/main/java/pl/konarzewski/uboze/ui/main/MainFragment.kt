package pl.konarzewski.uboze.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yuyakaido.android.cardstackview.*
import pl.konarzewski.uboze.R
import pl.konarzewski.uboze.adapter.CardStackAdapter


class MainFragment : Fragment() {

    private lateinit var cardStackView: CardStackView
    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: CardStackAdapter
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


        cardStackView = view.findViewById(R.id.card_stack_view)

        manager = CardStackLayoutManager(requireContext()!!.applicationContext, object: CardStackListener {
            /*
            @Override
            fun onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            fun onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right) {
                    Toast.makeText(MainActivity.this, "Direction Right", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top) {
                    Toast.makeText(MainActivity.this, "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left) {
                    Toast.makeText(MainActivity.this, "Direction Left", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom) {
                    Toast.makeText(MainActivity.this, "Direction Bottom", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            fun onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            fun onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            fun onCardAppeared(View view, int position) {
                TextView tv = view . findViewById (R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }

            @Override
            fun onCardDisappeared(View view, int position) {
                TextView tv = view . findViewById (R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }
            */
            override fun onCardDragging(direction: Direction?, ratio: Float) {
                TODO("Not yet implemented")
            }

            override fun onCardSwiped(direction: Direction?) {
                TODO("Not yet implemented")
            }

            override fun onCardRewound() {
                TODO("Not yet implemented")
            }

            override fun onCardCanceled() {
                TODO("Not yet implemented")
            }

            override fun onCardAppeared(view: View?, position: Int) {
                TODO("Not yet implemented")
            }

            override fun onCardDisappeared(view: View?, position: Int) {
                TODO("Not yet implemented")
            }
        });
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
        adapter = CardStackAdapter(engine);
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(DefaultItemAnimator());

    }
}
