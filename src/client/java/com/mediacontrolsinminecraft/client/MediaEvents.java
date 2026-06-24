package com.mediacontrolsinminecraft.client;

import com.mediacontrolsinminecraft.client.config.ConfigVariables;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

import static com.mediacontrolsinminecraft.client.MediaControlsInMinecraftClient.configVariables;

public class MediaEvents {

    //This needs to be set to this at the start so it doesn't run onMediaEnd when the game launches
    private static String lastMediaMetadata = "STATUS: No media being played";

    private static double originalInGameVolume = 100.0;

    //Media change
    public static void mediaChangeListener(){

        if (!lastMediaMetadata.equals(MediaMetadata.mediaMetaData) && MediaMetadata.mediaBeingPlayed){

            onMediaChange();

        }

        lastMediaMetadata = MediaMetadata.mediaMetaData;

    }

    private static void onMediaChange(){

        if ((!(MediaControlsInMinecraftClient.currentScreen instanceof PauseScreen) || !Minecraft.getInstance().isPaused()) && configVariables.showNowPlayingToastWhenMediaChanges){

            SystemToast.addOrUpdate(
                    Minecraft.getInstance().gui.toastManager(),
                    SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                    Component.nullToEmpty("Now Playing"),
                    Component.nullToEmpty(MediaMetadata.mediaMetaData)
            );

        }

    }

    //Media start
    public static void mediaStartListener(){

        if (lastMediaMetadata.equals(MediaMetadata.noMediaBeingPlayedMessage) && !MediaMetadata.mediaMetaData.equals(MediaMetadata.noMediaBeingPlayedMessage)){

            onMediaStart();

        }

    }

    private static void onMediaStart(){

        System.out.println("Media started");

        PauseScreenMediaButtons.initialize();

        Minecraft minecraftInstance = Minecraft.getInstance();

        if (configVariables.muteInGameMusicWhenPlayingMedia && minecraftInstance.options.getSoundSourceOptionInstance(SoundSource.MUSIC).get() != 0.0) {

            originalInGameVolume = minecraftInstance.options.getSoundSourceOptionInstance(SoundSource.MUSIC).get();

            minecraftInstance.options.getSoundSourceOptionInstance(SoundSource.MUSIC).set(0.0);
            minecraftInstance.options.save();

        }

    }

    //Media End
    public static void mediaEndListener(){

        if (!lastMediaMetadata.equals(MediaMetadata.noMediaBeingPlayedMessage) && MediaMetadata.mediaMetaData.equals(MediaMetadata.noMediaBeingPlayedMessage)){

            onMediaEnd();

        }

    }

    private static void onMediaEnd(){

        System.out.println("Media ended");

        if (configVariables.muteInGameMusicWhenPlayingMedia){

            Minecraft minecraftInstance = Minecraft.getInstance();

            minecraftInstance.options.getSoundSourceOptionInstance(SoundSource.MUSIC).set(originalInGameVolume);
            minecraftInstance.options.save();

        }

    }

    public static void registerShutdownHook(){

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {

            if (configVariables.muteInGameMusicWhenPlayingMedia && MediaMetadata.mediaBeingPlayed){

                client.options.getSoundSourceOptionInstance(SoundSource.MUSIC).set(originalInGameVolume);
                client.options.save();

            }

        });

    }

}
