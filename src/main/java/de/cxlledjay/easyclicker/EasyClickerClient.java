package de.cxlledjay.easyclicker;

import de.cxlledjay.easyclicker.clicker.AutoClicker;
import de.cxlledjay.easyclicker.clicker.ConfigManager;
import de.cxlledjay.easyclicker.event.KeyInputHandler;
import net.fabricmc.api.ClientModInitializer;

public class EasyClickerClient implements ClientModInitializer {



    @Override
    public void onInitializeClient() {

        // load config
        ConfigManager.readConfig();

        // init autoclicker and register events
        AutoClicker.init();

        // register keybindings
        KeyInputHandler.register();
    }
}
