package com.quickswaprebind.mixin;

import com.quickswaprebind.QuickSwapRebindClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into {@link HandledScreen} that makes the quick-move (shift-click) key rebindable.
 * <p>
 * Behaviour when the keybind is <b>still the default</b> (Left Shift):
 * <ul>
 *   <li>Does nothing — vanilla handles shift-click normally.</li>
 * </ul>
 * <p>
 * Behaviour when <b>rebound</b> to another key (e.g. Caps Lock):
 * <ul>
 *   <li>New key + click → QUICK_MOVE (shift-click equivalent)</li>
 *   <li>Shift + click → normal PICKUP (vanilla shift-click is suppressed)</li>
 *   <li>Plain click → passes through to vanilla (normal click)</li>
 * </ul>
 */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Shadow
    @Nullable
    protected Slot focusedSlot;

    @Shadow
    protected abstract void onMouseClick(@Nullable Slot slot, int slotId, int button, SlotActionType actionType);

    /**
     * 1.21.11+: mouseClicked now takes (Click, boolean) instead of (double, double, int).
     * Intercept at HEAD to handle our rebindable quick-move key.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void quickswaprebind$onMouseClicked(Click click, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        int button = click.button();

        // Only intercept left-click (0) and right-click (1) on a valid slot
        if ((button != 0 && button != 1) || this.focusedSlot == null) {
            return;
        }

        // If the keybind is still default (Left Shift), do nothing — let vanilla handle it.
        if (!QuickSwapRebindClient.isRebound()) {
            return;
        }

        // --- The key has been rebound to something other than Shift ---

        boolean customKeyHeld = QuickSwapRebindClient.isQuickSwapKeyPressed();
        boolean shiftHeld = QuickSwapRebindClient.isShiftDown();

        if (customKeyHeld) {
            // Custom key is held → perform QUICK_MOVE (exact same packet as shift-click)
            this.onMouseClick(this.focusedSlot, this.focusedSlot.id, button, SlotActionType.QUICK_MOVE);
            cir.setReturnValue(true);
        } else if (shiftHeld) {
            // Shift is held but we rebound AWAY from shift → suppress vanilla shift-click.
            // Perform a normal PICKUP instead so shift-click no longer quick-moves.
            this.onMouseClick(this.focusedSlot, this.focusedSlot.id, button, SlotActionType.PICKUP);
            cir.setReturnValue(true);
        }
        // Otherwise: no modifier held → fall through to vanilla (normal click)
    }
}
