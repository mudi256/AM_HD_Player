package org.videolan.hdplayeram.gui.tv.browser

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import org.videolan.hdplayeram.R
import org.videolan.hdplayeram.viewmodels.audio.AlbumModel


class AlbumsFragment : MediaLibBrowserFragment<AlbumModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = currentItem?.title ?: getString(R.string.albums)
        model = ViewModelProviders.of(this, AlbumModel.Factory(requireContext(), currentItem)).get(AlbumModel::class.java)
        model.dataset.observe(this, Observer { update(it!!) })
    }
}