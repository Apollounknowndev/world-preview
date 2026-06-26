package dev.worldgen.world.preview.client.gui.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class ToggleButton extends OldStyleImageButton {
    public boolean selected;
    protected final int xDiff;

    public ToggleButton(int x, int y, int width, int height, int xTexStart, int yTexStart, Identifier resourceLocation, OnPress onPress) {
        this(x, y, width, height, xTexStart, yTexStart, width, height, resourceLocation, 256, 256, onPress);
    }

    public ToggleButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int xDiff, int yDiff, Identifier resourceLocation, OnPress onPress) {
        this(x, y, width, height, xTexStart, yTexStart, xDiff, yDiff, resourceLocation, 256, 256, onPress);
    }

    public ToggleButton(
            int x,
            int y,
            int width,
            int height,
            int xTexStart,
            int yTexStart,
            int xDiff,
            int yDiff,
            Identifier resourceLocation,
            int texWidth,
            int texHeight,
            OnPress onPress
    ) {
        super(x, y, width, height, xTexStart, yTexStart, yDiff, resourceLocation, texWidth, texHeight, onPress);
        this.xDiff = xDiff;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = this.xTexStart;
        int y = this.yTexStart;
        if (!selected) {
            x += xDiff;
        }
        if (!this.isActive()) {
            y += yDiffTex * 2;
        } else if (this.isHoveredOrFocused()) {
            y += yDiffTex;
        }

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), x, y, width, height, texWidth, texHeight, ARGB.white(this.alpha));
    }

    @Override
    public void onPress(InputWithModifiers input) {
        selected = !selected;
        super.onPress(input);
    }
}
