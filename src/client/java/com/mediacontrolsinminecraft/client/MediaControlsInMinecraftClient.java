package com.mediacontrolsinminecraft.client;

import com.mediacontrolsinminecraft.client.config.ConfigManager;
import com.mediacontrolsinminecraft.client.config.ConfigVariables;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;

public class MediaControlsInMinecraftClient implements ClientModInitializer {

	public static Screen currentScreen;
	public static int currentScaledWidth;
	public static int currentScaledHeight;

	//Detect OS
	public static final String currentOperatingSystem = System.getProperty("os.name").toLowerCase();

	public static ConfigVariables configVariables = ConfigManager.loadFromConfigFile();

	@Override
	public void onInitializeClient() {

		MediaEvents.registerShutdownHook();

		ConfigManager.saveToConfigFile();

		//Choosing what to do based on OS
		if (isWindows()){

			MediaMetadata.startMediaMonitoringForWindows();

			//Checking if PowerShell crashed
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				if (!MediaMetadata.powershellRunning) MediaMetadata.startMediaMonitoringForWindows();
			});

		}
		else if (isLinux()) {

			MediaMetadata.startMediaMentoringForLinux();

		}
		else {

			System.err.println("Media Controls in Minecraft does not work on you operating system");

		}

		//Updating Now playing label
		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			MediaEvents.mediaStartListener();
			MediaEvents.mediaEndListener();
			MediaEvents.mediaChangeListener();

			PauseScreenMediaButtons.updateNowPlayingLabel(MediaMetadata.mediaMetaData);

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

	public static boolean isWindows(){

		return currentOperatingSystem.contains("win");

	}

	public static boolean isLinux(){

		return  currentOperatingSystem.contains("nix") || currentOperatingSystem.contains("nux") || currentOperatingSystem.contains("aix");

	}

}