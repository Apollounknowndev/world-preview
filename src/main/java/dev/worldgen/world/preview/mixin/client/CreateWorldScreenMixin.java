package dev.worldgen.world.preview.mixin.client;

import dev.worldgen.world.preview.WorldPreview;
import dev.worldgen.world.preview.client.gui.screens.PreviewTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >= 26.2 {
/*import net.minecraft.client.gui.components.tabs.MenuTabBar;
*///? }

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {

    //? if >= 26.2 {
    /*@Shadow private @Nullable MenuTabBar tabNavigationBar;
    *///? } else {
    @Shadow private @Nullable TabNavigationBar tabNavigationBar;
     //? }

    private PreviewTab previewTab;

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;",
                    shift = At.Shift.BEFORE
            ),
            slice = @Slice(
                    from = @At("HEAD"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;addToFooter(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;")
            )
    )
    private void appendPreviewTab(CallbackInfo ci) {
        previewTab = new PreviewTab((CreateWorldScreen) (Object) this, ((ScreenAccessor) this).getMinecraft());



        //? if >= 26.2 {
        /*final MenuTabBar originalRaw = tabNavigationBar;
        final TabNavigationBarAccessor original = (TabNavigationBarAccessor)originalRaw;
        var builder = MenuTabBar.builder(original.getTabManager(), originalRaw.getWidth());
        *///? } else {
        final TabNavigationBar originalRaw = tabNavigationBar;
        final TabNavigationBarAccessor original = (TabNavigationBarAccessor)originalRaw;
        var builder = TabNavigationBar.builder(original.getTabManager(), original.getWidth());
         //? }

        tabNavigationBar = builder
                .addTabs(original.getTabs().toArray(new Tab[0]))
                .addTabs(previewTab)
                .build();
    }

    @Inject(method = "popScreen", at = @At("HEAD"))
    private void saveConfigOnClose(CallbackInfo ci) {
        previewTab.close();
        WorldPreview.saveConfig();
    }

    @Inject(method = "onCreate", at = @At("HEAD"))
    private void saveConfigOnCreate(CallbackInfo ci) {
        previewTab.close();
        WorldPreview.saveConfig();
    }

}
