package com.ash1688.nuclearpowered.client;

import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.client.screen.BaseMachineScreen;
import com.ash1688.nuclearpowered.client.screen.FluidMachineScreen;
import com.ash1688.nuclearpowered.init.NPMenus;
import com.ash1688.nuclearpowered.machine.BaseMachineMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NuclearPowered.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class NPClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Item-only machines
            reg(NPMenus.ORE_CRUSHER.get(), "ore_crusher", 79, 35);
            reg(NPMenus.CUTTER.get(), "cutter", 79, 35);
            reg(NPMenus.MACERATOR.get(), "macerator", 79, 35);
            reg(NPMenus.SEPARATOR.get(), "separator", 75, 35);
            reg(NPMenus.MIXER.get(), "mixer", 100, 35);
            reg(NPMenus.FUEL_ROD_ASSEMBLY.get(), "fuel_rod_assembly", 100, 35);

            // Fluid-output machines (Diffusion, Centrifuge): fluid bar on right
            regFluid(NPMenus.GASEOUS_DIFFUSION.get(), "gaseous_diffusion", 79, 35,
                    false, 0, 0, true, 116, 14);
            regFluid(NPMenus.CENTRIFUGE.get(), "centrifuge", 79, 35,
                    false, 0, 0, true, 116, 14);
            // Fluid-input machine (Converter): fluid bar on left
            regFluid(NPMenus.CONVERTER.get(), "converter", 79, 35,
                    true, 52, 14, false, 0, 0);
        });
    }

    private static <M extends BaseMachineMenu> void reg(MenuType<M> type, String name, int arrowX, int arrowY) {
        MenuScreens.<M, BaseMachineScreen<M>>register(type,
                (menu, inv, title) -> new BaseMachineScreen<>(menu, inv, title, name, arrowX, arrowY));
    }

    private static <M extends BaseMachineMenu> void regFluid(MenuType<M> type, String name, int arrowX, int arrowY,
                                                              boolean showInputFluid, int inX, int inY,
                                                              boolean showOutputFluid, int outX, int outY) {
        MenuScreens.<M, FluidMachineScreen<M>>register(type,
                (menu, inv, title) -> new FluidMachineScreen<>(menu, inv, title, name, arrowX, arrowY,
                        showInputFluid, inX, inY, showOutputFluid, outX, outY));
    }

    private NPClientSetup() {}
}
