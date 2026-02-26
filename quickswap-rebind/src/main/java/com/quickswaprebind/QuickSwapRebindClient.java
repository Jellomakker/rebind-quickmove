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

    /**
     * The keybind for quick-dropping an entire stack from inventory.
     * Default: Unbound – set it in Controls.
     * Vanilla requires Ctrl+Q; this lets you do it with one key.
     */
    public static KeyBinding quickDropKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[QuickSwap Rebind] Initializing...");

        quickSwapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.quickswaprebind.quickmove",   // translation key
                InputUtil.Type.KEYSYM,              // key type
                GLFW.GLFW_KEY_G,                    // default key: G (rebindable in Controls)
                "category.quickswaprebind"           // category shown in Controls
        ));

        quickDropKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.quickswaprebind.quickdrop",    // translation key
                InputUtil.Type.KEYSYM,              // key type
                GLFW.GLFW_KEY_H,                    // default key: H (rebindable in Controls)
                "category.quickswaprebind"           // category shown in Controls
        ));

        LOGGER.info("[QuickSwap Rebind] Registered keybinds: Quick Move (default: G), Quick Drop (default: H)");
        LOGGER.info("[QuickSwap Rebind] Go to Options > Controls > Key Binds and look for 'QuickSwap Rebind' category");
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

    /**
     * Checks whether the key bound to quick-drop is physically held down.
     *
     * @return true if the quick-drop key is pressed right now
     */
    public static boolean isQuickDropKeyPressed() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) {
            return false;
        }
        long windowHandle = client.getWindow().getHandle();
        InputUtil.Key boundKey = ((KeyBindingAccessor) quickDropKey).quickswaprebind$getBoundKey();
        return InputUtil.isKeyPressed(windowHandle, boundKey.getCode());
    }
}
