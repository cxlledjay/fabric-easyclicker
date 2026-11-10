package de.cxlledjay.easyclicker;

import de.cxlledjay.easyclicker.clicker.AutoClicker;
import de.cxlledjay.easyclicker.config.EasyClickerConfig;
import de.cxlledjay.easyclicker.event.KeyInputHandler;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;

public class EasyClickerClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {

        // init config
        MidnightConfig.init(EasyClicker.MOD_ID, EasyClickerConfig.class);

        // init autoclicker and register events
        AutoClicker.init();

        // register keybindings
        KeyInputHandler.register();
    }
}
