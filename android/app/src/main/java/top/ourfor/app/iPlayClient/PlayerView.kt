package top.ourfor.app.iPlayClient

import android.content.Context
import android.graphics.Color
import android.widget.FrameLayout
import android.widget.TextView

class PlayerView(context: Context) : FrameLayout(context) {
    init {
        // set padding and background color
        setPadding(16,16,16,16)
        setBackgroundColor(Color.parseColor("#5FD3F3"))

        // add default text view
        addView(TextView(context).apply {
            text = "Welcome to Android Fragments with React Native."
        })
    }
}