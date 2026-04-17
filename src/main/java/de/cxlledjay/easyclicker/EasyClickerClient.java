package de.cxlledjay.easyclicker;

import de.cxlledjay.easyclicker.clicker.AutoClicker;
import de.cxlledjay.easyclicker.clicker.AutoClickerConfig;
import de.cxlledjay.easyclicker.event.KeyInputHandler;
import net.fabricmc.api.ClientModInitializer;

public class EasyClickerClient implements ClientModInitializer {

    public static AutoClickerConfig clickerConfig = new AutoClickerConfig(AutoClickerConfig.ClickMode.INDIVIDUAL, 1);

    @Override
    public void onInitializeClient() {

        AutoClicker.init();
        KeyInputHandler.register();
    }
}
