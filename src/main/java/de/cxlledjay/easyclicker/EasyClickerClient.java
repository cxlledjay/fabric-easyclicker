package de.cxlledjay.easyclicker;

import de.cxlledjay.easyclicker.event.KeyInputHandler;
import net.fabricmc.api.ClientModInitializer;

public class EasyClickerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        KeyInputHandler.register();
    }
}
