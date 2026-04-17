package de.cxlledjay.easyclicker.event;

import de.cxlledjay.easyclicker.clicker.AutoClicker;
import de.cxlledjay.easyclicker.gui.MenuScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_EASYCLICKER = "key.category.easyclicker.category";

    public static final String KEY_EASYCLICKER_MENU = "key.easyclicker.menu";
    public static final String KEY_EASYCLICKER_TOGGLE = "key.easyclicker.toggle";

    public static KeyBinding menu_key;
    public static KeyBinding toggle_key;


    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {


            if(menu_key.wasPressed()){
                // show menu screen
                MinecraftClient.getInstance().setScreen(
                        new MenuScreen()
                );
            }

            if(toggle_key.wasPressed()){
                //call toggle easy clicker method
                AutoClicker.toggleClicker();
            }
        });
    }


    public static void register() {
        menu_key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            KEY_EASYCLICKER_MENU,
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            KEY_CATEGORY_EASYCLICKER
        ));

        toggle_key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            KEY_EASYCLICKER_TOGGLE,
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            KEY_CATEGORY_EASYCLICKER
        ));

        registerKeyInputs();
    }
}
