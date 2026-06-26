package dev.worldgen.world.preview.reload.biomecolor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.world.preview.backend.color.PreviewData.DataSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public class BiomeColorEntry {
	public static final Codec<Integer> COLOR_CODEC = Codec.withAlternative(
		ExtraCodecs.STRING_RGB_COLOR,
		RecordCodecBuilder.create(i -> i.group(
			Codec.INT.fieldOf("r").forGetter(ARGB::red),
			Codec.INT.fieldOf("g").forGetter(ARGB::green),
			Codec.INT.fieldOf("b").forGetter(ARGB::blue)
		).apply(i, ARGB::color))
	);
	
	public DataSource dataSource;
	public int color;
	public Optional<Boolean> cave;
	public String name;
	
	public BiomeColorEntry(DataSource dataSource, int color, Optional<Boolean> cave, String name) {
		this.dataSource = dataSource;
		this.color = color;
		this.cave = cave;
		this.name = name;
	}
	
	public static Codec<BiomeColorEntry> createCodec(DataSource source) {
		return Codec.withAlternative(
			COLOR_CODEC.xmap(color -> create(source, color), e -> e.color),
			RecordCodecBuilder.create(i -> i.group(
				Codec.STRING.optionalFieldOf("name", "").forGetter(e -> e.name),
				COLOR_CODEC.fieldOf("color").forGetter(e -> e.color),
				Codec.BOOL.optionalFieldOf("cave").forGetter(e -> e.cave)
			).apply(i, (name, color, cave) -> new BiomeColorEntry(source, color, cave, name)))
		);
	}
	
	public BiomeColorEntry(DataSource dataSource, int color, boolean cave, String name) {
		this(dataSource, color, Optional.of(cave), name);
	}
	
	public static BiomeColorEntry create(DataSource source, int color) {
		return new BiomeColorEntry(source, color, false, null);
	}
	
	public static BiomeColorEntry create(String name, int color, Optional<Boolean> cave) {
		return new BiomeColorEntry(DataSource.RESOURCE, color, cave, name);
	}
}