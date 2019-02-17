package org.videolan.hdplayeram.gui.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

import org.videolan.hdplayeram.R
import org.videolan.hdplayeram.gui.helpers.UiTools

class AboutActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_main)
        UiTools.fillAboutView(window.decorView.rootView)
        TvUtil.applyOverscanMargin(this)
        this.registerTimeView(findViewById(R.id.tv_time))
    }
}
