package pl.konarzewski.uboze.ui.main.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import pl.konarzewski.uboze.R

class CardStackAdapter(private val paths: List<String>) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardStackAdapter.ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.word_item, parent, false))

    override fun onBindViewHolder(holder: CardStackAdapter.ViewHolder, position: Int) =
        holder.setData(paths.get(position))

    override fun getItemCount(): Int = paths.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView

        init {
            image = view.findViewById(R.id.image_place_holder)
        }

        fun setData(path: String) =
            image.setImageBitmap(BitmapFactory.decodeFile(path))
    }
}
