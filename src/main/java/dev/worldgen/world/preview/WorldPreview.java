package dev.worldgen.world.preview;

import dev.worldgen.world.preview.backend.WorkManager;
import dev.worldgen.world.preview.backend.color.PreviewMappingData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.worldgen.world.preview.reload.biomecolor.BiomeColorEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;


//? if fabric {
/*public static final Path CONFIG_FOLDER = FMLPaths.CONFIGDIR.get().resolve("world_preview");D
 *///? } else {
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
//? }

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class WorldPreview {
    public static final String MOD_ID = "world_preview";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    
    //? if fabric {
    /*public static final Path CONFIG_FOLDER = FMLPaths.CONFIGDIR.get().resolve("world_preview");D
    *///? } else {
    public static final Path CONFIG_FOLDER = FMLPaths.CONFIGDIR.get().resolve("world_preview");
    //? }
    

    private static final Path configFile = CONFIG_FOLDER.resolve("config.json");
    private static final Path renderConfigFile = CONFIG_FOLDER.resolve("renderConfig.json");
    private static final Path missingColorsFile = CONFIG_FOLDER.resolve("missing-colors.json");
    private static final Path missingStructuresFile = CONFIG_FOLDER.resolve("missing-structures.json");
    private static final Path userColorConfigFile = CONFIG_FOLDER.resolve("biome-colors.json");

    private static WorldPreviewConfig config;
    private static WorkManager workManager;
    private static PreviewMappingData previewMappingData;
    private static RenderSettings renderSettings;
    private static WorldPreview INSTANCE;

    public static WorldPreview get() {
        return INSTANCE;
    }

    public static void init() {
        if (!Files.exists(CONFIG_FOLDER)) {
            CONFIG_FOLDER.toFile().mkdirs();
        }

        loadConfig();

        renderSettings = new RenderSettings();
        workManager = new WorkManager(renderSettings, config);
        previewMappingData = new PreviewMappingData();
    }
    
    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }

    public static Executor serverThreadPoolExecutor() {
        return Executors.newSingleThreadExecutor(SidedThreadGroups.SERVER);
    }

    public static void loaderSpecificSetup(MinecraftServer minecraftServer) {
        ServerLifecycleHooks.handleServerAboutToStart(minecraftServer);
    }

    public static void loaderSpecificTeardown(MinecraftServer minecraftServer) {
        ServerLifecycleHooks.handleServerStopped(minecraftServer);
    }

    public static WorldPreviewConfig cfg() {
        return config;
    }

    public static WorkManager workManager() {
        return workManager;
    }

    public static PreviewMappingData biomeColorMap() {
        return previewMappingData;
    }

    public static RenderSettings renderSettings() {
        return renderSettings;
    }

    public static Path userColorConfigFile() {
        return userColorConfigFile;
    }

    public static boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    public static void loadConfig() {
        LOGGER.info("Loading config file: {}", configFile);
        try {
            if (!Files.exists(configFile)) {
                config = new WorldPreviewConfig();
            } else {
                config = GSON.fromJson(Files.readString(configFile), WorldPreviewConfig.class);
            }

            if (!Files.exists(renderConfigFile)) {
                renderSettings = new RenderSettings();
            } else {
                renderSettings = GSON.fromJson(Files.readString(renderConfigFile), RenderSettings.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveConfig() {
        LOGGER.info("Saving config file: {}", configFile);
        try {
            Files.writeString(configFile, GSON.toJson(config) + "\n");
            Files.writeString(renderConfigFile, GSON.toJson(renderSettings) + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeMissingColors(List<String> missing) {
        try {
            Files.deleteIfExists(missingColorsFile);
            if (missing.isEmpty()) {
                return;
            }
            LOGGER.warn("No entry mapping for {} biomes found. The list of biomes without a entry mapping can be found in {}", missing.size(), missingColorsFile);
            final String raw = GSON.toJson(missing);
            Files.writeString(missingColorsFile, raw + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeMissingStructures(List<String> missing) {
        try {
            Files.deleteIfExists(missingStructuresFile);
            if (missing.isEmpty()) {
                return;
            }
            LOGGER.warn("No structure data for {} structure found. The list of structures without data can be found in {}", missing.size(), missingStructuresFile);
            final String raw = GSON.toJson(missing);
            Files.writeString(missingStructuresFile, raw + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeUserColorConfig(Map<Identifier, BiomeColorEntry> userColorConfig) {
        record Entry(int r, int g, int b, boolean cave) {}
        Map<String, Entry> writeData = userColorConfig.entrySet()
                .stream()
                .collect(Collectors.toMap(x -> x.getKey().toString(), x -> {
                    BiomeColorEntry raw = x.getValue();
                    final int r = (raw.color >> 16) & 0xFF;
                    final int g = (raw.color >> 8) & 0xFF;
                    final int b = raw.color & 0xFF;
                    return new Entry(r, g, b, raw.cave.orElseThrow());
                }));

        final String raw = GSON.toJson(writeData);
        try {
            Files.writeString(userColorConfigFile, raw + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int nativeColor(int orig) {
        /*
        final int R = (orig >> 16) & 0xFF;
        final int G = (orig >> 8) & 0xFF;
        final int B = (orig >> 0) & 0xFF;
        return (R << 16) | (G << 8) | (B << 0) | (0xFF << 24);
         */
        return orig | (0xFF << 24);
    }
}
