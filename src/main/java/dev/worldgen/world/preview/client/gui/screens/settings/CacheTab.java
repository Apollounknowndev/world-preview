package dev.worldgen.world.preview.client.gui.screens.settings;

import dev.worldgen.world.preview.WorldPreview;
import dev.worldgen.world.preview.WorldPreviewConfig;
import dev.worldgen.world.preview.backend.storage.PreviewStorageCacheManager;
import dev.worldgen.world.preview.client.gui.widgets.WGLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;

import static dev.worldgen.world.preview.client.WorldPreviewComponents.SETTINGS_CACHE_CLEAR;
import static dev.worldgen.world.preview.client.WorldPreviewComponents.SETTINGS_CACHE_CLEAR_TOOLTIP;
import static dev.worldgen.world.preview.client.WorldPreviewComponents.SETTINGS_CACHE_COMPRESSION;
import static dev.worldgen.world.preview.client.WorldPreviewComponents.SETTINGS_CACHE_COMPRESSION_TOOLTIP;
import static dev.worldgen.world.preview.client.WorldPreviewComponents.SETTINGS_CACHE_DESC;
import static dev.worldgen.world.preview.client.WorldPreviewComponents.SETTINGS_CACHE_G_ENABLE;
import static dev.worldgen.world.preview.client.WorldPreviewComponents.SETTINGS_CACHE_N_ENABLE;
import static dev.worldgen.world.preview.client.WorldPreviewComponents.SETTINGS_CACHE_TITLE;
import static dev.worldgen.world.preview.client.gui.screens.PreviewContainer.LINE_HEIGHT;

public class CacheTab extends GridLayoutTab {

    private final PreviewStorageCacheManager cacheManager;

    public CacheTab(Minecraft minecraft, PreviewStorageCacheManager cacheManager) {
        super(SETTINGS_CACHE_TITLE);
        this.cacheManager = cacheManager;

        final WorldPreviewConfig cfg = WorldPreview.cfg();
        final int LINE_WIDTH = 320;

        Checkbox cbGameEnable     = Checkbox.builder(SETTINGS_CACHE_G_ENABLE,           minecraft.font).selected(cfg.cacheInGame      ).onValueChange((box, val) -> cfg.cacheInGame       = val).build();
        Checkbox cbNewEnable      = Checkbox.builder(SETTINGS_CACHE_N_ENABLE,           minecraft.font).selected(cfg.cacheInNew       ).onValueChange((box, val) -> cfg.cacheInNew        = val).build();
        Checkbox cbCompressEnable = Checkbox.builder(SETTINGS_CACHE_COMPRESSION,        minecraft.font).selected(cfg.enableCompression).onValueChange((box, val) -> cfg.enableCompression = val).build();

        cbCompressEnable.setTooltip(Tooltip.create(SETTINGS_CACHE_COMPRESSION_TOOLTIP));

        Button btnClear = Button
                .builder(SETTINGS_CACHE_CLEAR, this::onClearCache)
                .tooltip(Tooltip.create(SETTINGS_CACHE_CLEAR_TOOLTIP))
                .width(LINE_WIDTH)
                .build();

        GridLayout.RowHelper rowHelper = layout.rowSpacing(8).createRowHelper(1);
        rowHelper.addChild(new WGLabel(minecraft.font, 0, 0, LINE_WIDTH, LINE_HEIGHT, WGLabel.TextAlignment.CENTER, SETTINGS_CACHE_DESC, 0xFFFFFF));
        rowHelper.addChild(cbGameEnable);
        rowHelper.addChild(cbNewEnable);
        rowHelper.addChild(new WGLabel(minecraft.font, 0, 0, LINE_WIDTH, LINE_HEIGHT, WGLabel.TextAlignment.CENTER, Component.empty(), 0xFFFFFF));
        rowHelper.addChild(btnClear);
        rowHelper.addChild(new WGLabel(minecraft.font, 0, 0, LINE_WIDTH, LINE_HEIGHT, WGLabel.TextAlignment.CENTER, Component.empty(), 0xFFFFFF));
        rowHelper.addChild(new WGLabel(minecraft.font, 0, 0, LINE_WIDTH, LINE_HEIGHT, WGLabel.TextAlignment.CENTER, Component.empty(), 0xFFFFFF));
        rowHelper.addChild(cbCompressEnable);
    }

    private void onClearCache(Button btn) {
        cacheManager.clearCache();
    }
}
