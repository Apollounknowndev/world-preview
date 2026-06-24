package caeruleusTait.world.preview.client.gui.widgets.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;

import java.util.Collection;

public abstract class BaseObjectSelectionList<E extends BaseObjectSelectionList.Entry<E>> extends ObjectSelectionList<E> {
    protected BaseObjectSelectionList(Minecraft minecraft, int width, int height, int x, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    @Override
    public int getRowLeft() {
        return getX();
    }

    @Override
    public int getRowRight() {
        return getX() + width - 6;
    }

    @Override
    public int getRowWidth() {
        return this.width - 6;
    }

    @Override
    protected int scrollBarX() {
        return getRowRight();
    }

    @Override
    protected void extractSelection(GuiGraphicsExtractor graphics, E entry, int outlineColor) {
        int left = this.getRowLeft();
        int right = this.getRowRight();
        int rowTop = entry.getY();
        int innerHeight = entry.getHeight() - 4;
        graphics.fill(left, rowTop - 2, right, rowTop + innerHeight + 2, outlineColor);
        graphics.fill(left + 1, rowTop - 1, right - 1, rowTop + innerHeight + 1, -16777216);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);

        E hovered = getHovered();
        if (hovered != null && hovered.tooltip() != null) {
            Minecraft minecraft = Minecraft.getInstance();
            graphics.setTooltipForNextFrame(
                    minecraft.font,
                    hovered.tooltip().toCharSequence(minecraft),
                    DefaultTooltipPositioner.INSTANCE,
                    mouseX,
                    mouseY,
                    true
            );
        }
    }

    /**
     * Make public
     */
    @Override
    public void replaceEntries(Collection<E> entryList) {
        super.replaceEntries(entryList);
    }

    public abstract static class Entry<E extends Entry<E>> extends ObjectSelectionList.Entry<E> {
        public Tooltip tooltip() {
            return null;
        }
    }
}
