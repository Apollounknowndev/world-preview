package dev.worldgen.world.preview.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ScreenUtils {

    public static void setScreen(Minecraft minecraft, Screen screen) {
        //? if >= 26.2 {
        /*minecraft.gui.setScreen(screen);
        *///? } else {
        minecraft.setScreen(screen);
         //? }
    }

    public static Screen getScreen(Minecraft minecraft) {
        //? if >= 26.2 {
        /*return minecraft.gui.screen();
        *///? } else {
        return minecraft.screen;
         //? }
    }

}
