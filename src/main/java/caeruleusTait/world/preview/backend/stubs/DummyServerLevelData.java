package caeruleusTait.world.preview.backend.stubs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.ServerLevelData;

public class DummyServerLevelData implements ServerLevelData {
    @Override
    public String getLevelName() {
        return "dummy";
    }

    @Override
    public GameType getGameType() {
        return GameType.SPECTATOR;
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public void setInitialized(boolean initialized) {
    }

    @Override
    public boolean isAllowCommands() {
        return false;
    }

    @Override
    public void setGameType(GameType type) {
    }

    @Override
    public void setGameTime(long time) {
    }

    // LevelData methods

    @Override
    public LevelData.RespawnData getRespawnData() {
        return LevelData.RespawnData.DEFAULT;
    }

    @Override
    public long getGameTime() {
        return 0;
    }

    @Override
    public boolean isHardcore() {
        return false;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.HARD;
    }

    @Override
    public boolean isDifficultyLocked() {
        return false;
    }

    // WritableLevelData

    @Override
    public void setSpawn(LevelData.RespawnData respawnData) {
    }

    // NeoForge injects these day-time accessors into ServerLevelData; the preview does not advance
    // time, so return inert values (a non-zero per-tick rate avoids division by zero).
    public void setDayTimePerTick(float dayTimePerTick) {
    }

    public void setDayTimeFraction(float dayTimeFraction) {
    }

    public float getDayTimePerTick() {
        return 1.0f;
    }

    public float getDayTimeFraction() {
        return 0.0f;
    }
}
