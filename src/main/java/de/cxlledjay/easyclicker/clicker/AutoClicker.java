package de.cxlledjay.easyclicker.clicker;

import de.cxlledjay.easyclicker.EasyClicker;
import de.cxlledjay.easyclicker.config.EasyClickerConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class AutoClicker {

    // ----- tracking -----
    private static boolean enabled = false;
    private static int rmbBucket = 0;
    private static int rmbInterval = 0;



    // ----- interfaces -----

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(AutoClicker::execute);
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




    // ----- auto clicker code -----

    public static void disableClicker(ClientPlayerEntity player) {
        enabled = false;
        player.sendMessage(Text.literal(EasyClicker.MOD_ID + " disabled").formatted(Formatting.BOLD, Formatting.RED), true);

        // cleanup
        MinecraftClient.getInstance().options.useKey.setPressed(false);
        rmbBucket = 0;
        rmbInterval = 0;
    }

    private static void enableClicker(ClientPlayerEntity player) {
        enabled = true;
        player.sendMessage(Text.literal(EasyClicker.MOD_ID + " enabled").formatted(Formatting.BOLD, Formatting.GREEN), true);
    }


    private static void execute(MinecraftClient client) {

        // disabled => do nothing
        if(!enabled) return;

        // safety
        if (client.player == null || client.world == null || client.currentScreen != null) {
            return;
        }

        // rmb clicker execute
        if(EasyClickerConfig.rmbEnabled) {
            // delegate rmb click mode
            switch (EasyClickerConfig.rmbMode) {
                case HOLDING -> executeRmbHolding(client);
                case INDIVIDUAL -> executeRmbIndividual(client);
                case INTERVAL ->  executeRmbInterval(client);
            }
        }

        // TODO: lmb clicker execute
        if(EasyClickerConfig.lmbEnabled) {
            // TBD
        }
    }


    private static void executeRmbHolding(MinecraftClient client) {
        client.options.useKey.setPressed(true);
    }

    private static void executeRmbIndividual(MinecraftClient client) {
        client.options.useKey.setPressed(false);

        // leaky bucket
        rmbBucket += EasyClickerConfig.rmbIndividualCps;
        while(rmbBucket >= 20){
            artificialRightClick(client);
            rmbBucket -= 20;
        }
    }

    private static void executeRmbInterval(MinecraftClient client) {
        client.options.useKey.setPressed(false);
        // simple timer
        if(++rmbInterval >= EasyClickerConfig.rmbIntervalTime) {
            artificialRightClick(client);
            rmbInterval = 0;
        }
    }







    private static void artificialRightClick(MinecraftClient client) {
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
