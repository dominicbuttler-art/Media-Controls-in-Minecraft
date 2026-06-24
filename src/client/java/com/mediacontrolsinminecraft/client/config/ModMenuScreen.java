package com.mediacontrolsinminecraft.client.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static com.mediacontrolsinminecraft.client.MediaControlsInMinecraftClient.configVariables;

public class ModMenuScreen extends Screen {

    protected ModMenuScreen(Component title) {
        super(title);
    }

    public static Screen OpenScreen(Screen parent) {

        ConfigBuilder configBuilder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Media Controls in Minecraft config"));

        ConfigCategory configCategory = configBuilder.getOrCreateCategory(Component.literal("category.mediacontrolsinminecraft.configCategory"));

        ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();

        //showNowPlayingToastWhenMediaChanges toggle
        configCategory.addEntry(entryBuilder.startBooleanToggle(Component.literal("Show now playing toast when media changes"), configVariables.showNowPlayingToastWhenMediaChanges)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> configVariables.showNowPlayingToastWhenMediaChanges = newValue)
                .build());

        //muteInGameMusicWhenPlayingMedia toggle
        configCategory.addEntry(entryBuilder.startBooleanToggle(Component.literal("Mute in game music when playing media"), configVariables.muteInGameMusicWhenPlayingMedia)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Will mute the in game music when media is started to be played, and will automatically unmute in game music media is no longer being played"))
                .setSaveConsumer(newValue -> configVariables.muteInGameMusicWhenPlayingMedia = newValue)
                .build()
        );

        //mediaControlsYOffset
        configCategory.addEntry(entryBuilder.startIntField(Component.literal("Media Controls y offset"), configVariables.mediaControlsYOffset)
                .setDefaultValue(160)
                .setTooltip(Component.literal("Offset of current window height divided by 4"))
                .setSaveConsumer(newValue -> configVariables.mediaControlsYOffset = newValue)
                .build()
        );

        //mediaControlsWidth
        configCategory.addEntry(entryBuilder.startIntField(Component.literal("Media Controls Width"), configVariables.mediaControlsWidth)
                .setDefaultValue(88)
                .setSaveConsumer(newValue -> configVariables.mediaControlsWidth = newValue)
                .build()
        );

        //nowPlayingLabelYOffset
        configCategory.addEntry(entryBuilder.startIntField(Component.literal("Now Playing Label Y Offset"), configVariables.nowPlayingLabelYOffset)
                .setDefaultValue(142)
                .setTooltip(Component.literal("Offset of current window height divided by 4"))
                .setSaveConsumer(newValue -> {
                    configVariables.nowPlayingLabelYOffset = newValue;
                    ConfigManager.saveToConfigFile();
                })
                .build()
        );

        Screen screen = configBuilder.build();
        Minecraft.getInstance().gui.setScreen(screen);

        return screen;

    }
}
