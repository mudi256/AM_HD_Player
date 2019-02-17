/*****************************************************************************
 * MediaItemDetailsFragment.java
 *****************************************************************************
 * Copyright © 2014-2015 VLC authors, VideoLAN and VideoLabs
 * Author: Geoffrey Métais
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/
package org.videolan.hdplayeram.gui.tv;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import org.videolan.libvlc.util.AndroidUtil;
import org.videolan.medialibrary.media.MediaWrapper;
import org.videolan.hdplayeram.R;
import org.videolan.hdplayeram.gui.helpers.AudioUtil;
import org.videolan.hdplayeram.gui.helpers.UiTools;
import org.videolan.hdplayeram.gui.tv.audioplayer.AudioPlayerActivity;
import org.videolan.hdplayeram.gui.video.VideoPlayerActivity;
import org.videolan.hdplayeram.media.MediaUtils;
import org.videolan.hdplayeram.repository.BrowserFavRepository;
import org.videolan.hdplayeram.util.FileUtils;
import org.videolan.hdplayeram.util.WorkersKt;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnActionClickedListener;

import static org.videolan.hdplayeram.util.Constants.ACTION_REMOTE_STOP;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class MediaItemDetailsFragment extends DetailsSupportFragment {
    private static final String TAG = "MediaItemDetailsFragment";
    private static final int ID_PLAY = 1;
    private static final int ID_LISTEN = 2;
    private static final int ID_FAVORITE_ADD = 3;
    private static final int ID_FAVORITE_DELETE = 4;
    private static final int ID_BROWSE = 5;
    private static final int ID_DL_SUBS = 6;
    private static final int ID_PLAY_ALL = 7;
    private static final int ID_PLAY_FROM_START = 8;

    private BackgroundManager mBackgroundManager;
    private ArrayObjectAdapter mRowsAdapter;
    private MediaItemDetails mMedia;
    private MediaWrapper mMediaWrapper;
    private BrowserFavRepository mBrowserFavRepository;
    private boolean mMediaStarted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBackgroundManager = BackgroundManager.getInstance(requireActivity());
        mBackgroundManager.setAutoReleaseOnStop(false);
        mBrowserFavRepository = BrowserFavRepository.Companion.getInstance(requireContext());
        mMediaStarted = false;
        buildDetails();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mBackgroundManager.isAttached()) mBackgroundManager.attachToView(getView());
    }

    @Override
    public void onPause() {
        mBackgroundManager.release();
        super.onPause();
        if (mMediaStarted) {
            final Context ctx = getContext();
            if (ctx != null) {
                ctx.sendBroadcast(new Intent(ACTION_REMOTE_STOP));
            }
        }
    }

    private void buildDetails() {
        final Bundle extras = requireActivity().getIntent().getExtras();
        if (extras == null) return;
        mMedia = extras.getParcelable("item");
        boolean hasMedia = extras.containsKey("media");
        final ClassPresenterSelector selector = new ClassPresenterSelector();
        final MediaWrapper media = hasMedia ? (MediaWrapper) extras.getParcelable("media") : new MediaWrapper(AndroidUtil.LocationToUri(mMedia.getLocation()));
        if (!hasMedia){
            media.setDisplayTitle(mMedia.getTitle());
        }
        mMediaWrapper = media;
        setTitle(media.getTitle());

        final List<MediaWrapper> mediaList = null;
        // Attach your media item details presenter to the row presenter:
        FullWidthDetailsOverviewRowPresenter rowPresenter = new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());

        final FragmentActivity activity = requireActivity();
        final DetailsOverviewRow detailsOverview = new DetailsOverviewRow(mMedia);
        final Action actionAdd = new Action(ID_FAVORITE_ADD, getString(R.string.favorites_add));
        final Action actionDelete = new Action(ID_FAVORITE_DELETE, getString(R.string.favorites_remove));

        rowPresenter.setBackgroundColor(ContextCompat.getColor(activity, R.color.orange500));
        rowPresenter.setOnActionClickedListener(new OnActionClickedListener() {

            @Override
            public void onActionClicked(Action action) {
                switch ((int)action.getId()){
                    case ID_LISTEN:
                        MediaUtils.INSTANCE.openMedia(activity, mMediaWrapper);
                        mMediaStarted = true;
                        break;
                    case ID_PLAY:
                        mMediaStarted = false;
                        TvUtil.INSTANCE.playMedia(activity, media);
                        activity.finish();
                        break;
                    case ID_FAVORITE_ADD:
                        final Uri uri = Uri.parse(mMedia.getLocation());
                        final boolean local = "file".equals(uri.getScheme());
                        if (local) mBrowserFavRepository.addLocalFavItem(uri, mMedia.getTitle(), mMedia.getArtworkUrl());
                        else mBrowserFavRepository.addNetworkFavItem(uri, mMedia.getTitle(), mMedia.getArtworkUrl());
                        detailsOverview.removeAction(actionAdd);
                        detailsOverview.addAction(actionDelete);
                        mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
                        Toast.makeText(activity, R.string.favorite_added, Toast.LENGTH_SHORT).show();
                        break;
                    case ID_FAVORITE_DELETE:
                        WorkersKt.runIO(new Runnable() {
                            @Override
                            public void run() {
                                mBrowserFavRepository.deleteBrowserFav(Uri.parse(mMedia.getLocation()));
                            }
                        });
                        detailsOverview.removeAction(actionDelete);
                        detailsOverview.addAction(actionAdd);
                        mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
                        Toast.makeText(activity, R.string.favorite_removed, Toast.LENGTH_SHORT).show();
                        break;
                    case ID_BROWSE:
                        TvUtil.INSTANCE.openMedia(activity, media, null);
                        break;
                    case ID_DL_SUBS:
                        MediaUtils.INSTANCE.getSubs(requireActivity(), media);
                        break;
                    case ID_PLAY_ALL:
                        if (mediaList != null) {
                            int position = -1;
                            for (int i= 0; i < mediaList.size(); ++i)
                                if (media.equals(mediaList.get(i)))
                                    position = i;
                            Activity activity = getActivity();
                            MediaUtils.INSTANCE.openList(activity, mediaList, position);
                            if (media.getType() == MediaWrapper.TYPE_AUDIO)
                                getActivity().startActivity(new Intent(activity, AudioPlayerActivity.class));
                            getActivity().finish();
                        }
                        break;
                    case ID_PLAY_FROM_START:
                        mMediaStarted = false;
                        VideoPlayerActivity.start(getActivity(), media.getUri(), true);
                        activity.finish();
                        break;
                }
            }
        });
        selector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        selector.addClassPresenter(ListRow.class,
                new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(selector);
        WorkersKt.runIO(new Runnable() {
            @Override
            public void run() {
                final Bitmap cover = media.getType() == MediaWrapper.TYPE_AUDIO || media.getType() == MediaWrapper.TYPE_VIDEO
                ? AudioUtil.readCoverBitmap(mMedia.getArtworkUrl(), 512) : null;
                final Bitmap blurred = cover != null ? UiTools.blurBitmap(cover) : null;
                final boolean browserFavExists = mBrowserFavRepository.browserFavExists((Uri.parse(mMedia.getLocation())));
                final boolean isDir = media.getType() == MediaWrapper.TYPE_DIR;
                final boolean canSave = isDir && FileUtils.canSave(media);
                WorkersKt.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isDetached()) return;
                        final Activity context = getActivity();
                        if (context == null || context.isFinishing()) return;
                        final Resources res = getResources();
                        if (isDir) {
                            detailsOverview.setImageDrawable(ContextCompat.getDrawable(context, TextUtils.equals(media.getUri().getScheme(),"file")
                                    ? R.drawable.ic_menu_folder_big
                                    : R.drawable.ic_menu_network_big));
                            detailsOverview.setImageScaleUpAllowed(true);
                            detailsOverview.addAction(new Action(ID_BROWSE, res.getString(R.string.browse_folder)));
                            if (canSave) detailsOverview.addAction(browserFavExists ? actionDelete : actionAdd);

                        } else if (media.getType() == MediaWrapper.TYPE_AUDIO) {
                            // Add images and action buttons to the details view
                            if (cover == null)
                                detailsOverview.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_default_cone));
                            else
                                detailsOverview.setImageBitmap(context, cover);

                            detailsOverview.addAction(new Action(ID_PLAY, res.getString(R.string.play)));
                            detailsOverview.addAction(new Action(ID_LISTEN, res.getString(R.string.listen)));
                            if (mediaList != null && mediaList.contains(media))
                                detailsOverview.addAction(new Action(ID_PLAY_ALL, res.getString(R.string.play_all)));
                        } else if (media.getType() == MediaWrapper.TYPE_VIDEO) {
                            // Add images and action buttons to the details view
                            if (cover == null)
                                detailsOverview.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_default_cone));
                            else
                                detailsOverview.setImageBitmap(context, cover);

                            detailsOverview.addAction(new Action(ID_PLAY, res.getString(R.string.play)));
                            detailsOverview.addAction(new Action(ID_PLAY_FROM_START, res.getString(R.string.play_from_start)));
                            if (FileUtils.canWrite(media.getUri()))
                                detailsOverview.addAction(new Action(ID_DL_SUBS, res.getString(R.string.download_subtitles)));
                            if (mediaList != null && mediaList.contains(media))
                                detailsOverview.addAction(new Action(ID_PLAY_ALL, res.getString(R.string.play_all)));
                        }
                        mRowsAdapter.add(detailsOverview);
                        setAdapter(mRowsAdapter);
                        if (blurred != null) mBackgroundManager.setBitmap(blurred);
                    }
                });
            }
        });
    }
}
