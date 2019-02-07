package org.videolan.mudiAudioVideo.gui.tv.browser

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import org.videolan.mudiAudioVideo.R
import org.videolan.mudiAudioVideo.viewmodels.audio.AlbumModel


class AlbumsFragment : MediaLibBrowserFragment<AlbumModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = currentItem?.title ?: getString(R.string.albums)
        model = ViewModelProviders.of(this, AlbumModel.Factory(requireContext(), currentItem)).get(AlbumModel::class.java)
        model.dataset.observe(this, Observer { update(it!!) })
    }
}