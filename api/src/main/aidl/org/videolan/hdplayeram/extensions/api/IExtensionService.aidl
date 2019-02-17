package org.videolan.hdplayeram.extensions.api;

import org.videolan.hdplayeram.extensions.api.IExtensionHost;
import org.videolan.hdplayeram.extensions.api.VLCExtensionItem;

interface IExtensionService {
    // Protocol version 1
    oneway void onInitialize(int index, in IExtensionHost host);
    oneway void browse(String stringId);
    oneway void refresh();
}
