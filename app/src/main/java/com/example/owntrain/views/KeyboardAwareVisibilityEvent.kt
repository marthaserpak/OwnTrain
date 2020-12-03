package com.example.owntrain.views

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ScrollView
import kotlinx.android.synthetic.main.activity_login.view.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class KeyboardAwareVisibilityEvent(context: Context, attrs: AttributeSet) :
    ScrollView(context, attrs), KeyboardVisibilityEventListener {
    init {
        /** определяет должен ли ScrollView растягивать своё содержимое для заполнения области просмотра */
        isFillViewport = true

        // убирает отображение прокрутки
        isVerticalScrollBarEnabled = false
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        KeyboardVisibilityEvent.setEventListener(context as Activity, this)
    }

    override fun onVisibilityChanged(isOpen: Boolean) {
        if (isOpen) {
            scrollView.scrollTo(0, scrollView.bottom)
        } else {
            scrollView.scrollTo(0, scrollView.top)
        }
    }
}