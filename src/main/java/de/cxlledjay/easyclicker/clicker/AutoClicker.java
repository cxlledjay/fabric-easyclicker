package de.cxlledjay.easyclicker.clicker;

import de.cxlledjay.easyclicker.EasyClicker;
import de.cxlledjay.easyclicker.config.EasyClickerConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class AutoClicker {

    // ----- tracking -----
    private static boolean enabled = false;
    private static int rmbBucket = 0;
    private static int rmbInterval = 0;



    // ----- interfaces -----

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(AutoClicker::autoClickerEvent);
    }

    public static void toggleClicker() {

        // safety
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;

        // toggle
        enabled = !enabled;

        // start/stop execute
        if (enabled) {
            enableClicker(player);
        } else {
            disableClicker(player);
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void tickRmbClicker(MinecraftClient client) {
        rmbClickerExecute(client);
    }




    // ----- auto clicker code -----

    private static void disableClicker(ClientPlayerEntity player) {
        enabled = false;
        MinecraftClient.getInstance().options.useKey.setPressed(false);
        rmbBucket = 0;
        rmbInterval = 0;
        if(player != null) player.sendMessage(Text.literal(EasyClicker.MOD_ID + " disabled").formatted(Formatting.BOLD, Formatting.RED), true);
    }

    private static void enableClicker(ClientPlayerEntity player) {
        enabled = true;
        if(player != null) player.sendMessage(Text.literal(EasyClicker.MOD_ID + " enabled").formatted(Formatting.BOLD, Formatting.GREEN), true);
    }

    private static void autoClickerEvent(MinecraftClient client) {

        // only tick when enabled and not when holding
        if(!enabled || EasyClickerConfig.rmbOnlyWhileHolding) return;

        // safety
        if (client.player == null || client.world == null || client.currentScreen != null) return;

        // execute rmb clicker
        if(EasyClickerConfig.rmbEnabled) rmbClickerExecute(client);

        // execute lmb clicker
        if(EasyClickerConfig.lmbEnabled) lmbClickerExecute(client);
    }

    private static void rmbClickerExecute(MinecraftClient client) {
        // delegate rmb click mode
        switch (EasyClickerConfig.rmbMode) {
            case HOLDING -> executeRmbHolding(client);
            case INDIVIDUAL -> executeRmbIndividual(client);
            case INTERVAL ->  executeRmbInterval(client);
        }
    }

    private static void lmbClickerExecute(MinecraftClient client) {
        // TODO: lmb clicker execute
    }


    // rmb clicker helpers

    private static void executeRmbHolding(MinecraftClient client) {
        client.options.useKey.setPressed(true);
    }

    private static void executeRmbIndividual(MinecraftClient client) {
        // leaky bucket
        rmbBucket += EasyClickerConfig.rmbIndividualCps;
        while(rmbBucket >= 20){
            artificialRightClick(client);
            rmbBucket -= 20;
        }
    }

    private static void executeRmbInterval(MinecraftClient client) {
        // simple timer
        if(++rmbInterval >= EasyClickerConfig.rmbIntervalTime) {
            artificialRightClick(client);
            rmbInterval = 0;
        }
    }

    private static void artificialRightClick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;

        HitResult target = client.crosshairTarget;

        // loop trough hands
        for (Hand hand : Hand.values()) {

            // 1. Entity Interaction (e.g., Villager trading, rotating Item Frames, mounting pigs)
            if (target != null && target.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHit = (EntityHitResult) target;
                Entity entity = entityHit.getEntity();

                // Try precise location interaction first (required for Armor Stands)
                ActionResult result = client.interactionManager.interactEntityAtLocation(client.player, entity, entityHit, hand);

                // If that passes, try general entity interaction
                if (!result.isAccepted()) {
                    result = client.interactionManager.interactEntity(client.player, entity, hand);
                }

                // If the interaction succeeded, swing the hand and stop processing
                if (result.isAccepted()) {
                    if (result.shouldSwingHand()) client.player.swingHand(hand);
                    continue;
                }
            }


            // 2. Block Interaction (e.g., Placing blocks, opening chests, pressing buttons)
            if (target != null && target.getType() == HitResult.Type.BLOCK) {
                if (target instanceof BlockHitResult blockHit) {
                    ActionResult result = client.interactionManager.interactBlock(client.player, hand, blockHit);

                    if (result.isAccepted()) {
                        if (result.shouldSwingHand()) client.player.swingHand(hand);
                        continue;
                    }
                }
            }


            // 3. Item Use (e.g., Eating food, drinking potions, throwing Ender Pearls, shooting bows)
            // This runs if the player is looking at air (MISS) OR if looking at a block/entity but holding an item that doesn't interact with it.
            if (!client.player.getStackInHand(hand).isEmpty()) {
                ActionResult result = client.interactionManager.interactItem(client.player, hand);

                if (result.isAccepted()) {
                    if (result.shouldSwingHand()) client.player.swingHand(hand);
                }
            }


        }
    }
}
