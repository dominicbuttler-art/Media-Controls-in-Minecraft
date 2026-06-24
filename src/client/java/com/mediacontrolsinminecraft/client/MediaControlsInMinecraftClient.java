package com.mediacontrolsinminecraft.client;

import com.mediacontrolsinminecraft.client.config.ConfigManager;
import com.mediacontrolsinminecraft.client.config.ConfigVariables;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaControlsInMinecraftClient implements ClientModInitializer {

	public static Screen currentScreen;
	public static int currentScaledWidth;
	public static int currentScaledHeight;

	public static ConfigVariables configVariables = ConfigManager.loadFromConfigFile();

	@Override
	public void onInitializeClient() {

		MediaEvents.registerShutdownHook();

		MediaMetadata.startMediaMonitoring();

		ConfigManager.saveToConfigFile();

		//Updating Now playing label
		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			MediaEvents.mediaStartListener();
			MediaEvents.mediaEndListener();
			MediaEvents.mediaChangeListener();

			PauseScreenMediaButtons.updateNowPlayingLabel(MediaMetadata.mediaMetaData);

			//Checking if PowerShell crashed
			if (!MediaMetadata.powershellRunning) MediaMetadata.startMediaMonitoring();

		});

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {

			currentScreen = screen;
			currentScaledWidth = scaledWidth;
			currentScaledHeight = scaledHeight;

			// Makes buttons if on the pause screen
			if (screen instanceof PauseScreen){

				PauseScreenMediaButtons.initialize();

			}

		});
	}

}