package dev.worldgen.world.preview.backend.color;

import dev.worldgen.world.preview.WorldPreview;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static dev.worldgen.world.preview.WorldPreview.LOGGER;

public class HeightmapPresetReloadListener extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {
    private static final Gson GSON = new GsonBuilder().create();
    private static final FileToIdConverter LISTER = FileToIdConverter.json("heightmap_preview_presets");

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, JsonElement> res = new HashMap<>();
        for (Map.Entry<Identifier, Resource> entry : LISTER.listMatchingResources(resourceManager).entrySet()) {
            Identifier id = LISTER.fileToId(entry.getKey());
            try (Reader reader = entry.getValue().openAsReader()) {
                res.put(id, GsonHelper.fromJson(GSON, reader, JsonElement.class));
            } catch (IOException e) {
                LOGGER.error("Failed to load heightmap preset {}", id, e);
            }
        }
        return res;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        final PreviewMappingData previewMappingData = WorldPreview.biomeColorMap();
        previewMappingData.clearHeightmapPresets();

        LOGGER.debug("Loading heightmap presets:");
        for (Map.Entry<Identifier, JsonElement> entry : object.entrySet()) {
            final PreviewData.HeightmapPresetData value = GSON.fromJson(entry.getValue(), PreviewData.HeightmapPresetData.class);
            LOGGER.debug(" - {}: {} | {} to {}", entry.getKey(), value.name(), value.minY(), value.maxY());
            previewMappingData.addHeightmapPreset(value);
        }
    }
}
