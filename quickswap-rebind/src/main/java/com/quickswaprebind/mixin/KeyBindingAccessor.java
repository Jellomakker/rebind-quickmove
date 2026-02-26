package com.quickswaprebind.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor mixin that exposes the private {@code boundKey} field of {@link KeyBinding}.
 * <p>
 * We need this so we can read which physical key the player has bound for quick-move,
 * then check whether that key is currently held down via {@code InputUtil.isKeyPressed()}.
 */
@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {

    /**
     * @return the {@link InputUtil.Key} this keybind is currently bound to
     */
    @Accessor("boundKey")
    InputUtil.Key quickswaprebind$getBoundKey();
}
