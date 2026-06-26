package dev.worldgen.world.preview.reload.biomecolor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.worldgen.world.preview.WorldPreview;
import dev.worldgen.world.preview.backend.color.PreviewData;
import dev.worldgen.world.preview.backend.color.PreviewMappingData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.biome.Biome;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class BiomeColorManager extends SimplePreparableReloadListener<Map<ResourceKey<Biome>, BiomeColorEntry>> {
	public static final Identifier ID = WorldPreview.id("biome_colors");
	public static final BiomeColorManager INSTANCE = new BiomeColorManager();
	
	@Override
	protected Map<ResourceKey<Biome>, BiomeColorEntry> prepare(ResourceManager manager, ProfilerFiller filler) {
		PreviewMappingData previewData = WorldPreview.biomeColorMap();
		previewData.clearBiomes();
		
		for (String namespace : manager.getNamespaces()) {
			var resourceStack = manager.getResourceStack(Identifier.fromNamespaceAndPath(namespace, "worldgen/biome_colors.json"));
			for (Resource resource : resourceStack) {
				try {
					var biomeColors = getBiomeColors(namespace + " namespace", PreviewData.DataSource.RESOURCE, resource.openAsReader());
					previewData.addColors(biomeColors);
				} catch (IOException e) {
					WorldPreview.LOGGER.warn("Couldn't parse biome entry data from {} namespace: {}", namespace, e);
				}
			}
		}
		
		if (!Files.exists(WorldPreview.userColorConfigFile())) {
			return Map.of();
		}
		
		previewData.makeBiomeResourceOnlyBackup();
		
		try {
			var biomeColors = getBiomeColors("config file", PreviewData.DataSource.CONFIG, Files.newBufferedReader(WorldPreview.userColorConfigFile()));
			previewData.addColors(biomeColors);
		} catch (IOException e) {
			WorldPreview.LOGGER.warn("Couldn't parse biome entry data from config file: {}", e.getLocalizedMessage());
		}
		
		return Map.of();
	}
	
	private static Codec<Map<ResourceKey<Biome>, BiomeColorEntry>> createCodec(PreviewData.DataSource source) {
		return Codec.unboundedMap(
			ResourceKey.codec(Registries.BIOME),
			BiomeColorEntry.createCodec(source)
		);
	}
	
	private static Map<ResourceKey<Biome>, BiomeColorEntry> getBiomeColors(String name, PreviewData.DataSource dataSource, BufferedReader reader) {
		try {
			var dataResult = createCodec(dataSource).parse(JsonOps.INSTANCE, LenientJsonParser.parse(reader));
			if (dataResult.isSuccess()) {
				return dataResult.getOrThrow();
			} else {
				throw new IllegalStateException(dataResult.error().orElseThrow().message());
			}
		} catch (Exception e) {
			WorldPreview.LOGGER.warn("Couldn't parse biome entry data from {}: {}", name, e.getLocalizedMessage());
		}
		return Map.of();
	}
	
	@Override
	protected void apply(Map<ResourceKey<Biome>, BiomeColorEntry> data, ResourceManager manager, ProfilerFiller filler) {
	
	}
}
