package com.mediacontrolsinminecraft.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

import static com.mediacontrolsinminecraft.client.MediaControlsInMinecraftClient.configVariables;

public class ConfigManager {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "mediacontrolsiniminecraft.json");

    public static ConfigVariables loadFromConfigFile(){

        if (configFile.exists()){

            try (FileReader configReader = new FileReader(configFile)) {

                return gson.fromJson(configReader, ConfigVariables.class);

            }
            catch (IOException e){

                e.printStackTrace();

            }

        }
        else {

            //Called to create new config file
            saveToConfigFile();

            return new ConfigVariables(

                    true,
                    true,
                    160,
                    88,
                    142

            );

        }

        return null;

    }

    public static void saveToConfigFile(){

        try {

            if (configFile.createNewFile()){

                System.out.println("Config file for media-controls-in-minecraft created successfully");

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

        try {

            try (FileWriter configWriter = new FileWriter(configFile)) {

                gson.toJson(configVariables, configWriter);

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}
