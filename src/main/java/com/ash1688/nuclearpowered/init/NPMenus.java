package com.ash1688.nuclearpowered.init;

import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.machine.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class NPMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, NuclearPowered.MODID);

    public static final RegistryObject<MenuType<OreCrusherMenu>> ORE_CRUSHER =
            REGISTER.register("ore_crusher", () -> IForgeMenuType.create(OreCrusherMenu::fromNetwork));
    public static final RegistryObject<MenuType<CutterMenu>> CUTTER =
            REGISTER.register("cutter", () -> IForgeMenuType.create(CutterMenu::fromNetwork));
    public static final RegistryObject<MenuType<MaceratorMenu>> MACERATOR =
            REGISTER.register("macerator", () -> IForgeMenuType.create(MaceratorMenu::fromNetwork));
    public static final RegistryObject<MenuType<SeparatorMenu>> SEPARATOR =
            REGISTER.register("separator", () -> IForgeMenuType.create(SeparatorMenu::fromNetwork));
    public static final RegistryObject<MenuType<MixerMenu>> MIXER =
            REGISTER.register("mixer", () -> IForgeMenuType.create(MixerMenu::fromNetwork));
    public static final RegistryObject<MenuType<FuelRodAssemblyMenu>> FUEL_ROD_ASSEMBLY =
            REGISTER.register("fuel_rod_assembly", () -> IForgeMenuType.create(FuelRodAssemblyMenu::fromNetwork));
    public static final RegistryObject<MenuType<GaseousDiffusionMenu>> GASEOUS_DIFFUSION =
            REGISTER.register("gaseous_diffusion", () -> IForgeMenuType.create(GaseousDiffusionMenu::fromNetwork));
    public static final RegistryObject<MenuType<ConverterMenu>> CONVERTER =
            REGISTER.register("converter", () -> IForgeMenuType.create(ConverterMenu::fromNetwork));
    public static final RegistryObject<MenuType<CoalBoilerMenu>> COAL_BOILER =
            REGISTER.register("coal_boiler", () -> IForgeMenuType.create(CoalBoilerMenu::fromNetwork));
    public static final RegistryObject<MenuType<SteamEngineMenu>> STEAM_ENGINE =
            REGISTER.register("steam_engine", () -> IForgeMenuType.create(SteamEngineMenu::fromNetwork));

    private NPMenus() {}
}
