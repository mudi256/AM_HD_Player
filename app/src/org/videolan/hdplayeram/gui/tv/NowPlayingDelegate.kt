package org.videolan.hdplayeram.gui.tv

import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.videolan.libvlc.MediaPlayer
import org.videolan.medialibrary.media.DummyItem
import org.videolan.hdplayeram.PlaybackService
import org.videolan.hdplayeram.media.PlaylistManager
import org.videolan.hdplayeram.util.CATEGORY_NOW_PLAYING
import org.videolan.hdplayeram.util.EmptyPBSCallback


@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class NowPlayingDelegate(private val fragment: MainTvFragment): PlaybackService.Callback by EmptyPBSCallback {
    private var service: PlaybackService? = null
    private val observer = Observer<Boolean> { updateCurrent() }

    private val playbackServiceObserver = Observer<PlaybackService> { service ->
        if (service !== null) {
            this.service = service
            updateCurrent()
            service.addCallback(this)
        } else {
            this.service?.removeCallback(this)
            this.service = null
        }
    }

    init {
        PlaylistManager.showAudioPlayer.observe(fragment, observer)
        PlaybackService.service.observe(fragment, playbackServiceObserver)
    }

    fun onClear() {
        PlaylistManager.showAudioPlayer.removeObserver(observer)
    }

    override fun onMediaPlayerEvent(event: MediaPlayer.Event) {
        when (event.type) {
            MediaPlayer.Event.MediaChanged -> updateCurrent()
        }
    }

    private fun updateCurrent() {
        fragment.updateAudioCategories(service?.currentMediaWrapper?.let {
            DummyItem(CATEGORY_NOW_PLAYING, it.title, it.artist).apply { setArtWork(service?.coverArt) }
        })
        fragment.updateHistory()
    }
}
