//? if fabric {
/*package caeruleusTait.world.preview.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WorldPreviewClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
    }

    public static void renderTexture(GuiGraphicsExtractor graphics, AbstractTexture texture, double xMin, double yMin, double xMax, double yMax) {
        graphics.blit(
                texture.getTextureView(),
                texture.getSampler(),
                (int) xMin, (int) yMin, (int) xMax, (int) yMax,
                0.0F, 1.0F, 0.0F, 1.0F
        );
    }

    public static String toTitleCase(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }

        return Arrays
                .stream(input.split(" "))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }
}
*///? }