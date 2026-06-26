//? if fabric {
package dev.worldgen.world.preview.platform.fabric;

import dev.worldgen.world.preview.WorldPreview;
import dev.worldgen.world.preview.backend.color.ColormapReloadListener;
import dev.worldgen.world.preview.backend.color.HeightmapPresetReloadListener;
import dev.worldgen.world.preview.backend.color.StructureMapReloadListener;
import dev.worldgen.world.preview.reload.biomecolor.BiomeColorManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;

public class WorldPreviewFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        WorldPreview.init();
        
        ResourceLoader loader = ResourceLoader.get(PackType.SERVER_DATA);
        loader.registerReloadListener(BiomeColorManager.ID, BiomeColorManager.INSTANCE);
        loader.registerReloadListener(WorldPreview.id("structure_map"), new StructureMapReloadListener());
        loader.registerReloadListener(WorldPreview.id("heightmap_preset"), new HeightmapPresetReloadListener());
        loader.registerReloadListener(WorldPreview.id("colormap"), new ColormapReloadListener());
    }
}
//? }