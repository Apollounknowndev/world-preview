package dev.worldgen.world.preview.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PreviewCacheLoadingScreen extends Screen {
    protected PreviewCacheLoadingScreen(Component component) {
        super(component);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(Minecraft.getInstance().font, title, width / 2, height / 2, 0xFFFFFFFF);
    }
}
