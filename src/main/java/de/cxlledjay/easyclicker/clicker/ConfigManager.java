package de.cxlledjay.easyclicker.clicker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.cxlledjay.easyclicker.EasyClicker;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {

    // --------------------- <gson setup> ---------------------

    // get gson builder
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // setup file path
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), EasyClicker.MOD_ID + ".json");




    // --------------------- <singleton config object> ---------------------

    private static Config config = new Config(Config.ClickMode.HOLDING, 1);

    // read in config from memory
    public static void readConfig() {
        // edge case 1: the file doesn't exist yet (e.g., first time launching the mod)
        if (!CONFIG_FILE.exists()) {
            EasyClicker.LOGGER.info("Config not found. Generating default settings.");
            // Since our 'config' variable already has the default values, we just save it to create the file!
            save();
            return;
        }

        // file exists --> read file
        try (java.io.FileReader reader = new java.io.FileReader(CONFIG_FILE)) {

            // The Magic Line: GSON reads the file and magically overwrites our 'config' variable with the saved data
            config = GSON.fromJson(reader, Config.class);
            EasyClicker.LOGGER.info("Successfully loaded config!");

            // edge case 2: The file existed, but it was completely empty
            if (config == null) {
                config = new Config(Config.ClickMode.HOLDING, 1);
                save();
            }

        } catch (Exception e) {
            // edge case 3: The user messed up the JSON syntax (e.g., typed a letter instead of a number)
            EasyClicker.LOGGER.error("CRITICAL ERROR: Config file is corrupted! Falling back to safe defaults.");

            // We instantly reset to safe defaults so the game doesn't crash when it tries to read the broken data
            config = new Config(Config.ClickMode.HOLDING, 1);

            // Note: We intentionally DO NOT call save() here.
            // If the user made a typo, we want to leave their file alone so they can open it and fix their mistake!
        }

    }

    public static Config getInstance() {
        return config;
    }



    // --------------------- <persist/save logic> ---------------------

    public static void save() {
        // Safety step: Ensure the "config" folder actually exists before trying to write to it
        CONFIG_FILE.getParentFile().mkdirs();

        // 3. Write to the disk
        // The try(...) block automatically closes the file writer when it finishes.
        // If you don't close it, the file might get corrupted or locked!
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {

            // The Magic Line: GSON takes your Java object and translates it into JSON text
            GSON.toJson(config, writer);

            EasyClicker.LOGGER.info("Successfully saved config to disk!");

        } catch (IOException e) {
            EasyClicker.LOGGER.error("CRITICAL ERROR: Failed to save config file!");
        }
    }
}
