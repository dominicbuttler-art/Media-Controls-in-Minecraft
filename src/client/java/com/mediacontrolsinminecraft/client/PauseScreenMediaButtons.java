package com.mediacontrolsinminecraft.client;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;

import static com.mediacontrolsinminecraft.client.MediaControlsInMinecraftClient.configVariables;

public class PauseScreenMediaButtons {

    private static StringWidget nowPlaying;

    public static void initialize(){

        if (MediaMetadata.mediaBeingPlayed && MediaControlsInMinecraftClient.currentScreen instanceof PauseScreen) {
            updateNowPlayingLabel(MediaMetadata.mediaMetaData);

            //Pause and play Button
            Button pauseButton = Button.builder(
                    Component.literal("Play/Pause"), // Button text
                    btn -> {

                        KeyPreses.sendKeyPress(KeyPreses.VK_MEDIA_PLAY_PAUSE);

                    }
            )
            .bounds(MediaControlsInMinecraftClient.currentScaledWidth / 2 - configVariables.mediaControlsWidth / 2, MediaControlsInMinecraftClient.currentScaledHeight / 4 + configVariables.mediaControlsYOffset, configVariables.mediaControlsWidth, 20)
            .build();

            //Next track Button
            Button nextTrackButton = Button.builder(
                    Component.literal("Next Track"), // Button text
                            btn -> {

                                KeyPreses.sendKeyPress(KeyPreses.VK_MEDIA_NEXT_TRACK);

                            }
                    )
                    .bounds(MediaControlsInMinecraftClient.currentScaledWidth / 2 + configVariables.mediaControlsWidth / 2, MediaControlsInMinecraftClient.currentScaledHeight / 4 + configVariables.mediaControlsYOffset, configVariables.mediaControlsWidth, 20)
                    .build();

            //Previous track Button
            Button previousTrackButton = Button.builder(
                            Component.literal("Previous Track"), // Button text
                            btn -> {

                                KeyPreses.sendKeyPress(KeyPreses.VK_MEDIA_PREV_TRACK);

                            }
                    )
                    .bounds(MediaControlsInMinecraftClient.currentScaledWidth / 2 - (configVariables.mediaControlsWidth + configVariables.mediaControlsWidth / 2), MediaControlsInMinecraftClient.currentScaledHeight / 4 + configVariables.mediaControlsYOffset, configVariables.mediaControlsWidth, 20)
                    .build();

            Screens.getWidgets(MediaControlsInMinecraftClient.currentScreen).add(pauseButton);
            Screens.getWidgets(MediaControlsInMinecraftClient.currentScreen).add(nextTrackButton);
            Screens.getWidgets(MediaControlsInMinecraftClient.currentScreen).add(previousTrackButton);
        }

    }

    public static void updateNowPlayingLabel(String metaData){

        if (MediaControlsInMinecraftClient.currentScreen instanceof PauseScreen && MediaMetadata.mediaBeingPlayed) {

            Screens.getWidgets(MediaControlsInMinecraftClient.currentScreen).remove(nowPlaying);

            nowPlaying = new StringWidget(
                    MediaControlsInMinecraftClient.currentScaledWidth / 2 - (metaData.length() * 5)/2 ,
                    MediaControlsInMinecraftClient.currentScaledHeight / 4 + configVariables.nowPlayingLabelYOffset,
                    15 * metaData.length(),
                    1,
                    Component.literal(metaData),
                    MediaControlsInMinecraftClient.currentScreen.getFont());

            Screens.getWidgets(MediaControlsInMinecraftClient.currentScreen).add(nowPlaying);
        }

    }

}
