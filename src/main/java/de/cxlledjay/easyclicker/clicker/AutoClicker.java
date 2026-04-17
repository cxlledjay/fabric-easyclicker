package de.cxlledjay.easyclicker.clicker;

import de.cxlledjay.easyclicker.EasyClicker;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

// singleton instance of the autoclicker
public class AutoClicker {

    // --------------------- <tracking variables> ---------------------
    // toggled state tracking
    private static boolean enabled = false;

    // tracking click-speed via leaky bucket
    private static int cptBucket;                     //< leaky bucket



    // --------------------- <interfaces> ---------------------


    // enable / disable routine of autoclicker
    public static void toggleClicker() {
        // safety check
        if(MinecraftClient.getInstance().player == null) return;

        // toggle global variable used inside tick event
        enabled = !enabled;

        if (enabled) {
            enableClicker();
        } else {
            disableClicker();
        }
    }

    // init routine of autoclicker, registers clicker event
    public static void init() {
        // register autoclicker event
        ClientTickEvents.END_CLIENT_TICK.register(AutoClicker::execute);
    }




    // --------------------- <autoclicker logic> ---------------------

    public static void disableClicker() {
        // tracking
        enabled = false;

        // display mode change
        MinecraftClient.getInstance().player.sendMessage(Text.literal(EasyClicker.MOD_ID + " disabled").formatted(Formatting.BOLD, Formatting.RED), true);

        // cleanup
        MinecraftClient.getInstance().options.useKey.setPressed(false);
        cptBucket = 0;
    }

    private static void enableClicker() {
        // tracking
        enabled = true;

        // display mode change
        MinecraftClient.getInstance().player.sendMessage(Text.literal(EasyClicker.MOD_ID + " enabled").formatted(Formatting.BOLD, Formatting.GREEN), true);
    }


    private static void execute(MinecraftClient client) {
        // safeguard
        if(!enabled) return;

        // safety check
        if (client.player == null || client.world == null || client.currentScreen != null) {
            return;
        }

        // actual clicking, depending on mode
        switch (ConfigManager.getInstance().getClickMode()) {
            case HOLDING -> {
                client.options.useKey.setPressed(true);
            }
            case INDIVIDUAL -> {
                // make sure, use key is not pressed
                client.options.useKey.setPressed(false);

                // fill up bucket
                cptBucket += ConfigManager.getInstance().getSpeed();

                // leaky bucket algorithm
                while(cptBucket >= 20){
                    executeFastClick(client);
                    cptBucket -= 20;
                }
            }
        }
    }


    private static void executeFastClick(MinecraftClient client) {
        // get what player is looking at
        HitResult target = client.crosshairTarget;

        // check if it is a block
        if (target != null && target.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) target;

            // place blocks
            assert client.interactionManager != null;
            client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHit);
        }
    }


}
