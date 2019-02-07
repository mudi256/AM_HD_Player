package org.videolan.mudiAudioVideo.interfaces;


public interface Filterable {
    boolean enableSearchOption();
    void filter(String query);
    void restoreList();
    void setSearchVisibility(boolean visible);
    String getFilterQuery();
}
