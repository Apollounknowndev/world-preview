package caeruleusTait.world.preview.backend.compat;

import caeruleusTait.world.preview.WorldPreview;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.dimension.LevelStem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Replays Lithostitched's worldgen modifiers and biome injectors on the preview's loaded registries.
 * The preview never starts a real server, so Lithostitched never runs on its own. The apply hook is
 * internal, so it is resolved by reflection; nothing happens when Lithostitched is absent.
 */
public final class LithostitchedCompat {
    private static final String MOD_ID = "lithostitched";
    private static final String INTERNAL_HOOKS = "dev.worldgen.lithostitched.impl.LithostitchedInternalHooks";

    private static volatile boolean warned = false;

    private LithostitchedCompat() {
    }

    public static void apply(RegistryAccess registries, Registry<LevelStem> dimensions, long seed) {
        if (!WorldPreview.get().isModLoaded(MOD_ID)) {
            return;
        }
        try {
            Method hook = Class.forName(INTERNAL_HOOKS)
                    .getMethod("applyModifiersAndInjections", RegistryAccess.class, Registry.class, long.class);
            hook.invoke(null, registries, dimensions, seed);
        } catch (ClassNotFoundException | NoSuchMethodException tooOld) {
            // The registry hook landed in 1.7.12; earlier builds only expose the server entry point.
            warnOnce("Lithostitched is too old to reflect in the preview; update to 1.7.12 or newer", null);
        } catch (InvocationTargetException e) {
            warnOnce("Lithostitched threw while applying worldgen to the preview", e.getCause());
        } catch (IllegalAccessException e) {
            warnOnce("Could not apply Lithostitched worldgen to the preview", e);
        }
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
