/*****************************************************************************
 * GridFragment.java
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
package org.videolan.hdplayeram.gui.tv.browser;

import android.os.Bundle;
import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.VerticalGridPresenter;
import androidx.fragment.app.FragmentActivity;

import org.videolan.hdplayeram.gui.tv.CardPresenter;
import org.videolan.hdplayeram.gui.tv.browser.interfaces.BrowserFragmentInterface;

public class GridFragment extends VerticalGridSupportFragment implements BrowserFragmentInterface {

    protected static final String TAG = "VLC/GridFragment";

    private static final int NUM_COLUMNS = 4;

    protected ArrayObjectAdapter mAdapter;
    FragmentActivity mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(NUM_COLUMNS);
        setGridPresenter(gridPresenter);
        mAdapter = new ArrayObjectAdapter(new CardPresenter(mContext));
        mAdapter.clear();
        setAdapter(mAdapter);
    }

    @Override
    public void refresh() {}
}