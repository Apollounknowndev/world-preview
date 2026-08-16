package dev.worldgen.world.preview.mixin.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TabNavigationBar.class)
public interface TabNavigationBarAccessor {

    //? if < 26.2 {
        @Accessor
        int getWidth(); // Removed in 26.2; TabNavigationBar exposes a public getter instead.
    //? }

    @Accessor
    TabManager getTabManager();

    @Accessor
    ImmutableList<Tab> getTabs();

}
