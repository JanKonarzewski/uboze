package pl.konarzewski.uboze.ui.main

import android.app.Activity
import android.widget.TextView
import android.widget.Toast
import pl.konarzewski.uboze.R

fun Toast.showCustomToast(message: String, activity: Activity) {
    val layout = activity.layoutInflater.inflate(
        R.layout.toast_layout,
        activity.findViewById(R.id.toast_container)
    )

    // set the text of the TextView of the message
    val textView = layout.findViewById<TextView>(R.id.counter)
    textView.text = message

    // use the application extension function
    this.apply {
        duration = Toast.LENGTH_SHORT
        view = layout
        show()
    }
}
