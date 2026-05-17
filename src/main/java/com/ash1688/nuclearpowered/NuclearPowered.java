package com.ash1688.nuclearpowered;

import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPCreativeTabs;
import com.ash1688.nuclearpowered.init.NPFluids;
import com.ash1688.nuclearpowered.init.NPItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(NuclearPowered.MODID)
public final class NuclearPowered {
    public static final String MODID = "nuclearpowered";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NuclearPowered() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        NPItems.REGISTER.register(modBus);
        NPBlocks.REGISTER.register(modBus);
        NPFluids.FLUIDS.register(modBus);
        NPFluids.FLUID_TYPES.register(modBus);
        NPCreativeTabs.REGISTER.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NPConfig.COMMON_SPEC);

        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Nuclear Powered: registries wired");
    }
}
