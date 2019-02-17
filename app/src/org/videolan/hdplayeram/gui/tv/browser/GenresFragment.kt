package org.videolan.hdplayeram.gui.tv.browser

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import org.videolan.hdplayeram.R
import org.videolan.hdplayeram.viewmodels.audio.GenresModel


class GenresFragment : MediaLibBrowserFragment<GenresModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = currentItem?.title ?: getString(R.string.genres)
        model = ViewModelProviders.of(this, GenresModel.Factory(requireContext())).get(GenresModel::class.java)
        model.dataset.observe(this, Observer { update(it!!) })
    }
}