package org.videolan.mudiAudioVideo.extensions.api;

import org.videolan.mudiAudioVideo.extensions.api.IExtensionHost;
import org.videolan.mudiAudioVideo.extensions.api.VLCExtensionItem;

interface IExtensionService {
    // Protocol version 1
    oneway void onInitialize(int index, in IExtensionHost host);
    oneway void browse(String stringId);
    oneway void refresh();
}
