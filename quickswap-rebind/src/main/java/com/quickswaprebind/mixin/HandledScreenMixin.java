package com.quickswaprebind.mixin;

import com.quickswaprebind.QuickSwapRebindClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin into {@link HandledScreen} that replaces the vanilla {@code hasShiftDown()} check
 * used for quick-moving items with our custom rebindable key.
 * <p>
 * Vanilla behaviour:
 * <pre>
 *   if (Screen.hasShiftDown()) {
 *       actionType = SlotActionType.QUICK_MOVE;
 *   }
 * </pre>
 * <p>
 * After this mixin, the condition becomes:
 * <pre>
 *   if (QuickSwapRebindClient.isQuickSwapKeyPressed()) {
 *       actionType = SlotActionType.QUICK_MOVE;
 *   }
 * </pre>
 * <p>
 * This is entirely client-side. The resulting slot-click packet sent to the server is
 * identical to a normal shift-click, so no custom packets are needed and servers
 * cannot distinguish this from vanilla shift-click.
 */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    /**
     * Redirects every {@code Screen.hasShiftDown()} call inside {@code mouseClicked}
     * to instead check our custom keybind.
     */
    @Redirect(
            method = "mouseClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;hasShiftDown()Z"
            )
    )
    private boolean quickswaprebind$redirectQuickMoveKey() {
        return QuickSwapRebindClient.isQuickSwapKeyPressed();
    }

    /**
     * Also redirect in {@code mouseReleased} so that shift-drag (quick-moving multiple
     * stacks) respects the rebound key too.
     */
    @Redirect(
            method = "mouseReleased",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;hasShiftDown()Z"
            ),
            require = 0  // Not all versions call hasShiftDown here – gracefully skip
    )
    private boolean quickswaprebind$redirectQuickMoveKeyRelease() {
        return QuickSwapRebindClient.isQuickSwapKeyPressed();
    }
}
