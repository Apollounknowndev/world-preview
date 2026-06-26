package dev.worldgen.world.preview.reload.structureicon;

import com.mojang.datafixers.util.Either;
import dev.worldgen.world.preview.WorldPreview;
import dev.worldgen.world.preview.backend.color.PreviewData.DataSource;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.Optional;

public class StructureIconEntry {
	public static final Identifier UNKNOWN_ICON = WorldPreview.id("textures/structure_icon/unknown.png");
	
	public DataSource dataSource;
	public String name;
	public Either<Identifier, ResourceKey<Item>> textureOrItem;
	public Optional<Boolean> showByDefault;
}
