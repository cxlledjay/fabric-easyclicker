package de.cxlledjay.easyclicker.mixin;

import de.cxlledjay.easyclicker.clicker.AutoClicker;
import de.cxlledjay.easyclicker.config.EasyClickerConfig;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class RightClickCancelMixin {

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void onDoItemUse(CallbackInfo ci) {
        if(AutoClicker.isEnabled() && EasyClickerConfig.rmbEnabled && EasyClickerConfig.rmbOnlyWhileHolding) {
            MinecraftClient client = (MinecraftClient) (Object) this;
            boolean rmbHeld = client.options.useKey.isPressed();
            if (rmbHeld) {
                AutoClicker.tickRmbClicker(client);
                ci.cancel();
            }
        }
    }
}