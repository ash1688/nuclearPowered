package com.ash1688.nuclearpowered.client;

import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.client.screen.OreCrusherScreen;
import com.ash1688.nuclearpowered.init.NPMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NuclearPowered.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class NPClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(NPMenus.ORE_CRUSHER.get(), OreCrusherScreen::new);
        });
    }

    private NPClientSetup() {}
}
