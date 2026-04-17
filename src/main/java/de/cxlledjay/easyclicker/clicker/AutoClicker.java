package de.cxlledjay.easyclicker.clicker;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import static de.cxlledjay.easyclicker.EasyClickerClient.clickerConfig;

// singleton instance of the autoclicker
public class AutoClicker {

    // --------------------- <tracking variables> ---------------------
    // toggled state tracking
    private static boolean enabled = false;

    // tracking click-speed via leaky bucket
    private static int cpt = 20;           //< clicks per tick, scaled up by factor 20 / or clicks per second
    private static int cptBucket;                     //< leaky bucket



    // --------------------- <interfaces> ---------------------

    // set clicks per tick value
    // IMPORTANT: cpt are scaled up by 20, just use cps (clicks per second) value
    public static void setCpt(int newCpt) {
        cpt = newCpt;
    }


    // enable / disable routine of autoclicker
    public static void toggleClicker() {
        // toggle global variable used inside tick event
        enabled = !enabled;

        // cleanup upon disabling
        if(!enabled){
            MinecraftClient.getInstance().options.useKey.setPressed(false);
            cptBucket = cpt;
        }
    }

    // init routine of autoclicker, registers clicker event
    public static void init() {
        // register autoclicker event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // safeguard
            if(!enabled) return;

            // safety check
            if (client.player == null || client.world == null || client.currentScreen != null) {
                return;
            }

            // actual clicking, depending on mode
            switch (clickerConfig.getClickMode()) {
                case HOLDING -> {
                    client.options.useKey.setPressed(true);
                }
                case INDIVIDUAL -> {
                    // make sure, use key is not pressed
                    client.options.useKey.setPressed(false);

                    // fill up bucket
                    cptBucket += cpt;

                    // leaky bucket algorithm
                    while(cptBucket >= 20){
                        executeFastClick(client);
                        cptBucket -= 20;
                    }
                }
            }
        });
    }




    // --------------------- <autoclicker logic> ---------------------

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
