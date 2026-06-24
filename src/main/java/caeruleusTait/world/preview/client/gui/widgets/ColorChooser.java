package caeruleusTait.world.preview.client.gui.widgets;

import caeruleusTait.world.preview.client.WorldPreviewClient;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.CommonComponents;

import java.awt.*;

public class ColorChooser extends AbstractWidget implements AutoCloseable {

    public static final int INITIAL_SV_SQUARE_SIZE = 128;
    public static final int INITIAL_H_BAR_WIDTH = 16;
    public static final int SEPARATOR = 10;
    public static final int INITIAL_FINAL_COLOR_HEIGHT = 20;

    private int svSquareSize;
    private int hBarWidth;
    private int finalColorHeight;

    private float hue = 0f;
    private float saturation = 0f;
    private float value = 0f;

    private int argbColor = 0xFF000000;
    private int argbHueOnly = 0xFF000000;

    private ColorUpdater updater;

    private NativeImage svImg;
    private DynamicTexture svTexture;
    private float svTextureHue = Float.NaN;
    private NativeImage hueImg;
    private DynamicTexture hueTexture;

    public ColorChooser(int x, int y) {
        super(x, y, 10, 10, CommonComponents.EMPTY);
        svSquareSize = INITIAL_SV_SQUARE_SIZE;
        hBarWidth = INITIAL_H_BAR_WIDTH;
        finalColorHeight = INITIAL_FINAL_COLOR_HEIGHT;
        recalculateSize();
    }

    private void recalculateSize() {
        width = svSquareSize + SEPARATOR + hBarWidth;
        height = svSquareSize + SEPARATOR + finalColorHeight;
    }

    public void setSquareSize(int squareSize) {
        float scalor = (float) squareSize / (float) INITIAL_SV_SQUARE_SIZE;
        svSquareSize = squareSize;
        hBarWidth = (int)(INITIAL_H_BAR_WIDTH * scalor);
        finalColorHeight = (int)(INITIAL_FINAL_COLOR_HEIGHT * scalor);
        recalculateSize();
        closeTextures();
    }

    private void closeTextures() {
        if (svTexture != null) {
            svTexture.close();
            svTexture = null;
        }
        if (svImg != null) {
            svImg.close();
            svImg = null;
        }
        if (hueTexture != null) {
            hueTexture.close();
            hueTexture = null;
        }
        if (hueImg != null) {
            hueImg.close();
            hueImg = null;
        }
        svTextureHue = Float.NaN;
    }

    @Override
    public void close() {
        closeTextures();
    }

    private void updateSvTexture() {
        if (svTexture == null) {
            svImg = new NativeImage(NativeImage.Format.RGBA, svSquareSize, svSquareSize, true);
            svTexture = new DynamicTexture(() -> "world_preview:color_chooser_sv", svImg);
        }
        if (svTextureHue == hue) {
            return;
        }
        // Horizontal axis is saturation (0..1), vertical axis is value (1 at top, 0 at bottom).
        for (int py = 0; py < svSquareSize; ++py) {
            float v = 1f - ((float) py / (float) (svSquareSize - 1));
            for (int px = 0; px < svSquareSize; ++px) {
                float s = (float) px / (float) (svSquareSize - 1);
                svImg.setPixelABGR(px, py, abgr(Color.HSBtoRGB(hue, s, v)));
            }
        }
        svTexture.upload();
        svTextureHue = hue;
    }

    private void updateHueTexture() {
        if (hueTexture != null) {
            return;
        }
        hueImg = new NativeImage(NativeImage.Format.RGBA, 1, svSquareSize, true);
        hueTexture = new DynamicTexture(() -> "world_preview:color_chooser_hue", hueImg);
        // Hue runs from 1 at the top to 0 at the bottom.
        for (int py = 0; py < svSquareSize; ++py) {
            float h = 1f - ((float) py / (float) (svSquareSize - 1));
            hueImg.setPixelABGR(0, py, abgr(Color.HSBtoRGB(h, 1f, 1f)));
        }
        hueTexture.upload();
    }

    /**
     * NativeImage stores pixels as ABGR, while {@link Color#HSBtoRGB} returns ARGB.
     */
    private static int abgr(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int i, int j, float f) {
        // render background
        graphics.fill(getX() - 2, getY() - 2, getX() + width + 2, getY() + height + 2, 0x77000000);

        updateSvTexture();
        updateHueTexture();

        // Render saturation value chooser
        int leftX = getX();
        int topY = getY();
        int rightX = leftX + svSquareSize;
        int botY = topY + svSquareSize;

        WorldPreviewClient.renderTexture(graphics, svTexture, leftX, topY, rightX, botY);

        // Render saturation value indicator
        int satX = leftX + Math.round(saturation * svSquareSize);
        int valY = topY + Math.round((1f - value) * svSquareSize);
        graphics.fill(satX - 4, valY - 4, satX + 4, valY + 4, value > .3 ? 0xFF000000 : 0xFFFFFFFF);
        graphics.fill(satX - 3, valY - 3, satX + 3, valY + 3, argbColor);

        // Render Hue chooser
        leftX = rightX + SEPARATOR;
        rightX = leftX + hBarWidth;

        WorldPreviewClient.renderTexture(graphics, hueTexture, leftX, topY, rightX, botY);

        // Render saturation value indicator
        int hueY = topY + Math.round((1f - hue) * svSquareSize);
        graphics.fill(leftX - 2, hueY - 4, rightX + 2, hueY + 4, 0xFF000000);
        graphics.fill(leftX - 1, hueY - 3, rightX + 1, hueY + 3, argbHueOnly);

        // Render final color box
        graphics.fill(getX(), botY + SEPARATOR, getX() + width, getY() + height, argbColor);
    }

    public boolean mouseEvent(double mouseX, double mouseY, MouseButtonInfo buttonInfo, boolean playSound) {
        if (!this.active || !this.visible || !isValidClickButton(buttonInfo) || !isMouseOver(mouseX, mouseY)) {
            return false;
        }
        if (Minecraft.getInstance().screen != null) {
            Minecraft.getInstance().screen.setFocused(this);
        }

        double leftX = getX();
        double topY = getY();
        double rightX = leftX + svSquareSize;
        double botY = topY + svSquareSize;

        boolean updated = false;

        // check if mouse in SV selector
        if (mouseX >= leftX && mouseX <= rightX && mouseY >= topY && mouseY <= botY) {
            if (playSound) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
            }
            value = 1f - (float) ((mouseY - topY) / (botY - topY));
            saturation = (float) ((mouseX - leftX) / (rightX - leftX));
            updated = true;
        }

        leftX = rightX + SEPARATOR;
        rightX = leftX + hBarWidth;

        // check if mouse in hue selector
        if (mouseX >= leftX && mouseX <= rightX && mouseY >= topY && mouseY <= botY) {
            if (playSound) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
            }
            hue = 1f - (float) ((mouseY - topY) / (botY - topY));
            updated = true;
        }

        argbColor = Color.HSBtoRGB(hue, saturation, value);
        argbHueOnly = Color.HSBtoRGB(hue, 1f, 1f);
        if (updated) {
            runUpdater();
        }
        return updated;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return mouseEvent(event.x(), event.y(), event.buttonInfo(), true);
    }

    @Override
    protected void onDrag(MouseButtonEvent event, double dragX, double dragY) {
        mouseEvent(event.x(), event.y(), event.buttonInfo(), false);
    }

    public void runUpdater() {
        if (updater == null) {
            return;
        }

        updater.doUpdate(
                (int) (hue * 360f),
                (int) (saturation * 100f),
                (int) (value * 100f)
        );
    }

    public void setUpdater(ColorUpdater updater) {
        this.updater = updater;
    }

    public void updateHSV(int h, int s, int v) {
        hue = (float) h / 360f;
        saturation = (float) s / 100f;
        value = (float) v / 100f;
        argbColor = Color.HSBtoRGB(hue, saturation, value);
        argbHueOnly = Color.HSBtoRGB(hue, 1f, 1f);
        runUpdater();
    }

    public void updateRGB(int rgb) {
        final int r = (rgb >> 16) & 0xFF;
        final int g = (rgb >> 8) & 0xFF;
        final int b = (rgb >> 0) & 0xFF;

        final float[] hsv = Color.RGBtoHSB(r, g, b, null);
        hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];
        argbColor = Color.HSBtoRGB(hue, saturation, value);
        argbHueOnly = Color.HSBtoRGB(hue, 1f, 1f);
        runUpdater();
    }

    public int colorRGB() {
        return argbColor & 0x00FFFFFF;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // Do nothing
    }

    public interface ColorUpdater {
        void doUpdate(int h, int s, int v);
    }
}
