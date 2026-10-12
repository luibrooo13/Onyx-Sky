package de.onyx.skytoggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/** K = Rauch, O = Obsidian, J = Basis-Sky, L = Tag (/time set day, braucht OP/Cheats). */
public class OnyxSkyToggleClient implements ClientModInitializer {
    private static final String BASE = "file/Onyx-Sky-1-Basis.zip";
    private static final String SMOKE = "file/Onyx-Sky-2-Rauch.zip";
    private static final String OBBY = "file/Onyx-Sky-3-Obsidian.zip";

    private KeyBinding smokeKey, obbyKey, dayKey, baseKey;

    @Override
    public void onInitializeClient() {
        KeyBinding.Category cat = KeyBinding.Category.create(Identifier.of("onyxskytoggle", "onyxsky"));
        smokeKey = reg("key.onyxsky.smoke", GLFW.GLFW_KEY_K, cat);
        obbyKey  = reg("key.onyxsky.obsidian", GLFW.GLFW_KEY_O, cat);
        dayKey   = reg("key.onyxsky.day", GLFW.GLFW_KEY_L, cat);
        baseKey  = reg("key.onyxsky.base", GLFW.GLFW_KEY_J, cat);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (smokeKey.wasPressed()) toggle(mc, SMOKE, "Rauch");
            while (obbyKey.wasPressed())  toggle(mc, OBBY, "Obsidian");
            while (baseKey.wasPressed())  toggle(mc, BASE, "Basis-Sky");
            while (dayKey.wasPressed()) {
                if (mc.player != null) mc.player.networkHandler.sendChatCommand("time set day");
            }
        });
    }

    private KeyBinding reg(String id, int key, KeyBinding.Category cat) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(id, InputUtil.Type.KEYSYM, key, cat));
    }

    private void toggle(MinecraftClient mc, String packId, String label) {
        ResourcePackManager m = mc.getResourcePackManager();
        boolean on = m.getEnabledIds().contains(packId);
        if (on) m.disable(packId); else m.enable(packId);
        mc.options.refreshResourcePacks(m);   // laedt Ressourcen neu (kurzer Ruckler)
        if (mc.player != null)
            mc.player.sendMessage(Text.literal("Onyx Sky: " + label + (on ? " AUS" : " AN")), true);
    }
}
