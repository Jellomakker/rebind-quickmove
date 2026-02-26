package com.quickswaprebind;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import com.quickswaprebind.mixin.KeyBindingAccessor;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-side mod that allows rebinding the inventory quick-move (shift-click) key.
 * <p>
 * By default, Minecraft uses Left Shift to quick-move items in inventories.
 * This mod exposes that as a normal re-bindable key in Options → Controls → Key Binds.
 */
public class QuickSwapRebindClient implements ClientModInitializer {

    public static final String MOD_ID = "quickswaprebind";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /**
     * The keybind that replaces Shift for quick-move in inventories.
     * Default: Left Shift (same as vanilla) – change it in Controls.
     */
    public static KeyBinding quickSwapKey;

    @Override
    public void onInitializeClient() {
        quickSwapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.quickswaprebind.quickmove",   // translation key
                InputUtil.Type.KEYSYM,              // key type
                GLFW.GLFW_KEY_LEFT_SHIFT,           // default key (vanilla behaviour)
                "category.quickswaprebind"           // category shown in Controls
        ));

        LOGGER.info("[QuickSwap Rebind] Registered quick-move keybind (default: Left Shift)");
    }

    /**
     * Checks whether the key currently bound to quick-move is physically held down.
     * Called from the {@link com.quickswaprebind.mixin.HandledScreenMixin} redirect.
     *
     * @return true if the bound key is pressed right now
     */
    public static boolean isQuickSwapKeyPressed() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) {
            return false;
        }
        long windowHandle = client.getWindow().getHandle();
        InputUtil.Key boundKey = ((KeyBindingAccessor) quickSwapKey).quickswaprebind$getBoundKey();
        return InputUtil.isKeyPressed(windowHandle, boundKey.getCode());
    }
}
