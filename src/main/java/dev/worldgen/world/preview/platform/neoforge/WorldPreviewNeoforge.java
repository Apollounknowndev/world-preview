//? if neoforge {
/*package dev.worldgen.world.preview.platform.neoforge;


import dev.worldgen.world.preview.WorldPreview;
import dev.worldgen.world.preview.backend.color.*;
import dev.worldgen.world.preview.reload.biomecolor.BiomeColorManager;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

@Mod(WorldPreview.MOD_ID)
public class WorldPreviewNeoforge {
    public WorldPreviewNeoforge() {
        
        WorldPreview.init();
        
        NeoForge.EVENT_BUS.addListener(this::registerReloadListeners);
    }

    private void registerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(BiomeColorManager.ID, BiomeColorManager.INSTANCE);
        event.addListener(Identifier.fromNamespaceAndPath("world_preview", "structure_map"), new StructureMapReloadListener());
        event.addListener(Identifier.fromNamespaceAndPath("world_preview", "heightmap_preset"), new HeightmapPresetReloadListener());
        event.addListener(Identifier.fromNamespaceAndPath("world_preview", "colormap"), new ColormapReloadListener());
    }
}
*///? }