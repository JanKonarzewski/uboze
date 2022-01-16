package pl.konarzewski.uboze.ui.main

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyakaido.android.cardstackview.*
import pl.konarzewski.uboze.R
import pl.konarzewski.uboze.database.entity.Imige

fun getConfiguredCardStackManager(ctx: Context, swipe: (Int) -> Unit): CardStackLayoutManager {
    val manager = getCardStackManager(ctx, swipe)
    manager.setStackFrom(StackFrom.None)
    manager.setVisibleCount(3)
    manager.setTranslationInterval(8.0f)
    manager.setScaleInterval(0.95f)
    manager.setSwipeThreshold(0.3f)
    manager.setMaxDegree(20.0f)
    manager.setDirections(Direction.FREEDOM)
    manager.setCanScrollHorizontal(true)
    manager.setSwipeableMethod(SwipeableMethod.Manual)
    manager.setOverlayInterpolator(LinearInterpolator())
    return manager
}

private fun getCardStackManager(ctx: Context, swipe: (Int) -> Unit): CardStackLayoutManager =
    CardStackLayoutManager(ctx, object : CardStackListener {
        override fun onCardDragging(direction: Direction?, ratio: Float) {}

        override fun onCardSwiped(direction: Direction?) {}

        override fun onCardRewound() {}

        override fun onCardCanceled() {}

        override fun onCardAppeared(view: View?, position: Int) {}

        override fun onCardDisappeared(view: View?, position: Int) =
            swipe(position)
    })

class CardStackAdapter(private val imiges: List<Imige>) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.word_item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.setData(imiges[position])

    override fun getItemCount(): Int = imiges.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView
        var counter: TextView
        var repNo: TextView

        init {
            image = view.findViewById(R.id.image_place_holder)
            counter = view.findViewById(R.id.counter)
            repNo = view.findViewById(R.id.rep_no)
        }

        fun setData(imige: Imige) {
            image.setImageBitmap(BitmapFactory.decodeFile(imige.path))
            counter.text = (imiges.size - adapterPosition).toString()
            repNo.text = (imige.rep_no?.plus(1)).toString()
        }
    }
}
