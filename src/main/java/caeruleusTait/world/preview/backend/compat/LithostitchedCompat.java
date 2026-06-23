package caeruleusTait.world.preview.backend.compat;

import caeruleusTait.world.preview.WorldPreview;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Replays Lithostitched's modifier pass on the preview's dummy server, which never starts a real
 * server and so never triggers Lithostitched on its own. The apply entry point is internal and
 * changes between Lithostitched versions, so it is resolved by reflection; nothing happens when
 * Lithostitched is absent.
 */
public final class LithostitchedCompat {
    private static final String MOD_ID = "lithostitched";

    // Apply entry points across Lithostitched versions, newest first.
    private static final String INTERNAL_HOOKS = "dev.worldgen.lithostitched.impl.LithostitchedInternalHooks";
    private static final String MODIFIER_MANAGER = "dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager";
    private static final String MODIFIER = "dev.worldgen.lithostitched.worldgen.modifier.Modifier";
    private static final String SURFACE_RULE_MANAGER = "dev.worldgen.lithostitched.worldgen.surface.SurfaceRuleManager";

    private static volatile boolean warned = false;

    private LithostitchedCompat() {
    }

    public static void apply(MinecraftServer server) {
        if (!WorldPreview.get().isModLoaded(MOD_ID)) {
            return;
        }
        try {
            // Newest releases: one hook applies modifiers, surface rules and injectors.
            if (callStatic(INTERNAL_HOOKS, "onServerAboutToStart", server)) {
                return;
            }

            // Older: apply-all moved from Modifier to ModifierManager in 1.6; surface rules are separate.
            boolean modifiers = callStatic(MODIFIER_MANAGER, "applyModifiers", server)
                    || callStatic(MODIFIER, "applyModifiers", server);
            boolean surfaceRules = callStatic(SURFACE_RULE_MANAGER, "applySurfaceRules", server);

            if (!modifiers && !surfaceRules) {
                warnOnce("Lithostitched is installed but no known worldgen apply hook was found; the preview will not reflect its modifiers", null);
            }
        } catch (InvocationTargetException e) {
            warnOnce("Lithostitched threw while applying worldgen modifiers to the preview", e.getCause());
        } catch (ReflectiveOperationException e) {
            warnOnce("Could not apply Lithostitched worldgen modifiers to the preview", e);
        }
    }

    private static boolean callStatic(String className, String method, MinecraftServer server) throws ReflectiveOperationException {
        final Method m;
        try {
            m = Class.forName(className).getMethod(method, MinecraftServer.class);
        } catch (ClassNotFoundException | NoSuchMethodException notPresent) {
            return false;
        }
        m.invoke(null, server);
        return true;
    }

    private static void warnOnce(String message, Throwable t) {
        if (warned) {
            return;
        }
        warned = true;
        if (t != null) {
            WorldPreview.LOGGER.warn(message, t);
        } else {
            WorldPreview.LOGGER.warn(message);
        }
    }
}
