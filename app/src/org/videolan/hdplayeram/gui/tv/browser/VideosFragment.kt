package org.videolan.hdplayeram.gui.tv.browser

import androidx.lifecycle.Observer
import android.os.Bundle
import org.videolan.hdplayeram.R
import org.videolan.hdplayeram.util.KEY_GROUP
import org.videolan.hdplayeram.viewmodels.VideosModel


class VideosFragment : MediaLibBrowserFragment<VideosModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = arguments?.getString(KEY_GROUP)
        title = group ?: getString(R.string.videos)
        model = VideosModel.get(requireContext(), this, group = group)
        model.dataset.observe(this, Observer { update(it!!) })
    }

    fun sort(sort: Int) = model.sort(sort)
}