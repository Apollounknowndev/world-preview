package dev.worldgen.world.preview.client.gui.screens.settings;

import dev.worldgen.world.preview.RenderSettings;
import dev.worldgen.world.preview.WorldPreview;
import dev.worldgen.world.preview.client.gui.widgets.WGLabel;
import dev.worldgen.world.preview.client.gui.widgets.lists.BaseObjectSelectionList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static dev.worldgen.world.preview.client.WorldPreviewComponents.SETTINGS_DIM_HEAD;
import static dev.worldgen.world.preview.client.WorldPreviewComponents.SETTINGS_DIM_TITLE;
import static dev.worldgen.world.preview.client.gui.screens.PreviewContainer.LINE_HEIGHT;
import static dev.worldgen.world.preview.client.gui.screens.PreviewContainer.LINE_VSPACE;

public class DimensionsTab implements Tab {
    private final Minecraft minecraft;
    private final RenderSettings renderSettings;

    private final WGLabel headLabel;
    private final DimensionList dimensionList;

    public DimensionsTab(Minecraft minecraft, List<Identifier> levelStemKeys) {
        this.minecraft = minecraft;
        this.renderSettings = WorldPreview.renderSettings();

        headLabel = new WGLabel(minecraft.font, 0, 0, 256, LINE_HEIGHT, WGLabel.TextAlignment.CENTER, SETTINGS_DIM_HEAD, 0xFFFFFFFF);
        dimensionList = new DimensionList(minecraft, 256, 100, 0, 0);
        dimensionList.replaceEntries(levelStemKeys.stream().map(dimensionList::entryFactory).toList());
        dimensionList.select(renderSettings.dimension);
    }

    @Override
    public @NotNull Component getTabTitle() {
        return SETTINGS_DIM_TITLE;
    }

    @Override
    public @NotNull Component getTabExtraNarration() {
        return Component.empty();
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> consumer) {
        consumer.accept(headLabel);
        consumer.accept(dimensionList);
    }

    @Override
    public void doLayout(ScreenRectangle rectangle) {
        int width = Math.min(rectangle.width() - 8, 256);
        int center = rectangle.left() + (rectangle.width() / 2);
        int left = center - width / 2;
        int top = rectangle.top() + LINE_VSPACE;
        int bottom = rectangle.bottom() - 16;

        headLabel.setWidth(width);
        headLabel.setPosition(left, top);

        top += LINE_HEIGHT + LINE_VSPACE;
        dimensionList.updateSizeAndPosition(width, bottom - top, left, top);
    }

    //? if >= 26.2 {
        /*@Override
        public Layout getLayout() {
            return null;
        }
    *///?}

    public class DimensionList extends BaseObjectSelectionList<DimensionList.DimensionEntry> {
        public DimensionList(Minecraft minecraft, int width, int height, int x, int y) {
            super(minecraft, width, height, x, y, 16);
        }

        public DimensionEntry entryFactory(Identifier dimensionKey) {
            return new DimensionEntry(dimensionKey);
        }

        public void select(Identifier dimensionKey) {
            for (DimensionEntry entry : children()) {
                if (entry.dimensionKey.equals(dimensionKey)) {
                    setSelected(entry);
                    return;
                }
            }
            setSelected(null);
        }

        public class DimensionEntry extends BaseObjectSelectionList.Entry<DimensionEntry> {
            private final Identifier dimensionKey;
            private final Component component;

            public DimensionEntry(Identifier dimensionKey) {
                this.dimensionKey = dimensionKey;
                this.component = Component.literal(dimensionKey.toString());
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.literal("");
            }

            @Override
            public void extractContent(
                    GuiGraphicsExtractor graphics,
                    int mouseX,
                    int mouseY,
                    boolean hovered,
                    float partialTick
            ) {
                graphics.text(minecraft.font, component, getX() + 5, getY() + 2, 0xFFFFFFFF);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                if (event.button() != 0) {
                    return false;
                }

                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                renderSettings.dimension = dimensionKey;
                return true;
            }
        }
    }
}
