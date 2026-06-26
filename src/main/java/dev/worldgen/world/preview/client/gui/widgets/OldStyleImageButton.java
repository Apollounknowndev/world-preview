package dev.worldgen.world.preview.client.gui.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class OldStyleImageButton extends Button {

    protected final int xTexStart;
    protected final int yTexStart;
    protected final int yDiffTex;
    protected final Identifier texture;
    protected final int texWidth;
    protected final int texHeight;

    public OldStyleImageButton(
            int x, int y, int width, int height,
            int xTexStart, int yTexStart, int yDiffTex,
            Identifier texture, int texWidth, int texHeight,
            OnPress onPress
    ) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
        this.texture = texture;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = this.xTexStart;
        int y = this.yTexStart;
        if (!this.isActive()) {
            y += yDiffTex * 2;
        } else if (this.isHoveredOrFocused()) {
            y += yDiffTex;
        }

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), x, y, width, height, texWidth, texHeight, ARGB.white(this.alpha));
    }
}
