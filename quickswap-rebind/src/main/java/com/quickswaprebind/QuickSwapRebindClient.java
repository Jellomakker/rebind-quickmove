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
 * <p>
 * When rebound to a different key:
 * <ul>
 *   <li>New key + click = QUICK_MOVE (shift-click equivalent)</li>
 *   <li>Shift + click = normal click (vanilla shift-click is suppressed)</li>
 * </ul>
 * When left at default (Left Shift): vanilla behaviour is unchanged.
 */
public class QuickSwapRebindClient implements ClientModInitializer {

    public static final String MOD_ID = "quickswaprebind";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /** The rebindable keybind for quick-move. Default: Left Shift. */
    public static KeyBinding quickSwapKey;

    /** GLFW key code for Left Shift – the vanilla default. */
    private static final int DEFAULT_KEY = GLFW.GLFW_KEY_LEFT_SHIFT;

    @Override
    public void onInitializeClient() {
        quickSwapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.quickswaprebind.quickmove",   // translation key
                InputUtil.Type.KEYSYM,              // key type
                DEFAULT_KEY,                        // default key (vanilla behaviour)
                "category.quickswaprebind"           // category shown in Controls
        ));

        LOGGER.info("[QuickSwap Rebind] Registered quick-move keybind (default: Left Shift)");
    }

    /**
     * @return the GLFW key code currently bound to quick-move
     */
    public static int getBoundKeyCode() {
        return ((KeyBindingAccessor) quickSwapKey).quickswaprebind$getBoundKey().getCode();
    }

    /**
     * @return true if the player has rebound quick-move away from Left Shift
     */
    public static boolean isRebound() {
        return getBoundKeyCode() != DEFAULT_KEY;
    }

    /**
     * Checks whether the key currently bound to quick-move is physically held down.
     *
     * @return true if the bound key is pressed right now
     */
    public static boolean isQuickSwapKeyPressed() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) {
            return false;
        }
        long handle = client.getWindow().getHandle();
        return InputUtil.isKeyPressed(handle, getBoundKeyCode());
    }
}
