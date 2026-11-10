package de.cxlledjay.easyclicker;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyClicker implements ModInitializer {

	public static final String MOD_ID = "easyclicker";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// nothing. client side only mod!
	}
}