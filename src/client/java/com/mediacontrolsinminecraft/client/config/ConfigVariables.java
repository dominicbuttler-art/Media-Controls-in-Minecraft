package com.mediacontrolsinminecraft.client.config;

public class ConfigVariables {

    public boolean showNowPlayingToastWhenMediaChanges;
    public boolean muteInGameMusicWhenPlayingMedia;

    public int mediaControlsYOffset;
    public int mediaControlsWidth;

    public int nowPlayingLabelYOffset;

    public ConfigVariables(

            boolean showNowPlayingToastWhenMediaChanges,
            boolean muteInGameMusicWhenPlayingMedia,
            int mediaControlsYOffset,
            int mediaControlsWidth,
            int nowPlayingLabelYOffset

    ){

        this.showNowPlayingToastWhenMediaChanges = showNowPlayingToastWhenMediaChanges;
        this.muteInGameMusicWhenPlayingMedia = muteInGameMusicWhenPlayingMedia;
        this.mediaControlsYOffset = mediaControlsYOffset;
        this.mediaControlsWidth = mediaControlsWidth;
        this.nowPlayingLabelYOffset = nowPlayingLabelYOffset;

    }

}
