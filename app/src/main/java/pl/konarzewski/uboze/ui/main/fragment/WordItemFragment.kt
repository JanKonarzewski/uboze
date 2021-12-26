package pl.konarzewski.uboze.ui.main.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import pl.konarzewski.uboze.R

class WordItemFragment(private val path: String) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.word_item, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) =
        (view.findViewById(R.id.image_place_holder) as ImageView)
            .setImageBitmap(BitmapFactory.decodeFile(path))

}