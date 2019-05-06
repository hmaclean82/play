package hmac.play.utils

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

object DisplayUtils {

    fun screenSize(context: Context): Point {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val screenSize = Point()
        display.getSize(screenSize)
        return screenSize
    }
}