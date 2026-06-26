package dev.worldgen.world.preview.backend.worker;

import com.mojang.datafixers.util.Pair;
import dev.worldgen.world.preview.backend.storage.PreviewSection;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.List;

public record WorkResult(
        WorkUnit workUnit,
        int quartY,
        PreviewSection section,
        List<BlockResult> results,
        List<Pair<Identifier, StructureStart>> structures
) {

    public record BlockResult(int quartX, int quartZ, short value) {}
}
