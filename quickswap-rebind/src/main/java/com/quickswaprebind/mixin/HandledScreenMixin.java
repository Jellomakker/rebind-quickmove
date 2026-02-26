package com.quickswaprebind.mixin;

import com.quickswaprebind.QuickSwapRebindClient;
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
 * Mixin into {@link HandledScreen} that adds rebindable quick-move support.
 * <p>
 * Uses {@code @Inject} at the HEAD of {@code mouseClicked} to intercept clicks
 * when our custom key is held, and performs a QUICK_MOVE action on the hovered slot.
 * <p>
 * This approach is version-resilient: it does not depend on where or how vanilla
 * checks {@code hasShiftDown()} internally — it simply fires before vanilla logic.
 * The resulting slot-click packet sent to the server is identical to a vanilla
 * shift-click, so servers cannot distinguish this from normal behaviour.
 */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Shadow
    @Nullable
    protected Slot focusedSlot;

    @Shadow
    protected abstract void onMouseClick(@Nullable Slot slot, int slotId, int button, SlotActionType actionType);

    /**
     * Before vanilla processes a mouse click in the inventory, check if our
     * custom quick-move key is held and the cursor is over a slot.
     * If so, perform QUICK_MOVE and cancel the vanilla handler.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void quickswaprebind$onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        // Quick Drop: hold the quick-drop key and click a slot to throw the entire stack
        // Vanilla equivalent: hover over slot and press Ctrl+Q
        // SlotActionType.THROW with button=1 means "drop entire stack"
        if (button == 0
                && this.focusedSlot != null
                && this.focusedSlot.hasStack()
                && QuickSwapRebindClient.isQuickDropKeyPressed()) {
            this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 1, SlotActionType.THROW);
            cir.setReturnValue(true);
            return;
        }

        // Quick Move: hold the quick-move key and click to shift-click
        if ((button == 0 || button == 1)
                && this.focusedSlot != null
                && QuickSwapRebindClient.isQuickSwapKeyPressed()) {
            this.onMouseClick(this.focusedSlot, this.focusedSlot.id, button, SlotActionType.QUICK_MOVE);
            cir.setReturnValue(true);
        }
    }
}
