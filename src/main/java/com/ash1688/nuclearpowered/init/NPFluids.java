package com.ash1688.nuclearpowered.init;

import com.ash1688.nuclearpowered.NuclearPowered;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Fluid registrations: FluidType (definition) + Source + Flowing.
 *
 * No in-world LiquidBlock or BucketItem for MVP — fluids only exist in machine tanks
 * and are transported by pipe mods (Mekanism, Thermal, AE2) via Forge Fluid caps.
 * Buckets will be added in M10 polish for testing without a pipe mod.
 */
public final class NPFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, NuclearPowered.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, NuclearPowered.MODID);

    // ===== UF6 (Uranium Hexafluoride, gas treated as fluid) =====
    public static final RegistryObject<FluidType> UF6_TYPE =
            FLUID_TYPES.register("uf6", () -> new FluidType(
                    FluidType.Properties.create()
                            .descriptionId("fluid_type.nuclearpowered.uf6")
                            .density(-150)  // negative => rises like a gas
                            .viscosity(800)
                            .temperature(330)
                            .lightLevel(0)));
    public static final RegistryObject<Fluid> UF6 = FLUIDS.register("uf6",
            () -> new ForgeFlowingFluid.Source(NPFluids.UF6_PROPERTIES));
    public static final RegistryObject<Fluid> UF6_FLOWING = FLUIDS.register("flowing_uf6",
            () -> new ForgeFlowingFluid.Flowing(NPFluids.UF6_PROPERTIES));

    // ===== Steam =====
    public static final RegistryObject<FluidType> STEAM_TYPE =
            FLUID_TYPES.register("steam", () -> new FluidType(
                    FluidType.Properties.create()
                            .descriptionId("fluid_type.nuclearpowered.steam")
                            .density(-500)
                            .viscosity(200)
                            .temperature(550)
                            .lightLevel(1)));
    public static final RegistryObject<Fluid> STEAM = FLUIDS.register("steam",
            () -> new ForgeFlowingFluid.Source(NPFluids.STEAM_PROPERTIES));
    public static final RegistryObject<Fluid> STEAM_FLOWING = FLUIDS.register("flowing_steam",
            () -> new ForgeFlowingFluid.Flowing(NPFluids.STEAM_PROPERTIES));

    // ===== Tritium =====
    public static final RegistryObject<FluidType> TRITIUM_TYPE =
            FLUID_TYPES.register("tritium", () -> new FluidType(
                    FluidType.Properties.create()
                            .descriptionId("fluid_type.nuclearpowered.tritium")
                            .density(70)
                            .viscosity(400)
                            .temperature(295)
                            .lightLevel(2)));
    public static final RegistryObject<Fluid> TRITIUM = FLUIDS.register("tritium",
            () -> new ForgeFlowingFluid.Source(NPFluids.TRITIUM_PROPERTIES));
    public static final RegistryObject<Fluid> TRITIUM_FLOWING = FLUIDS.register("flowing_tritium",
            () -> new ForgeFlowingFluid.Flowing(NPFluids.TRITIUM_PROPERTIES));

    // Properties are built lazily via Suppliers so the registry objects above resolve.
    public static final ForgeFlowingFluid.Properties UF6_PROPERTIES =
            new ForgeFlowingFluid.Properties(UF6_TYPE, UF6, UF6_FLOWING);
    public static final ForgeFlowingFluid.Properties STEAM_PROPERTIES =
            new ForgeFlowingFluid.Properties(STEAM_TYPE, STEAM, STEAM_FLOWING);
    public static final ForgeFlowingFluid.Properties TRITIUM_PROPERTIES =
            new ForgeFlowingFluid.Properties(TRITIUM_TYPE, TRITIUM, TRITIUM_FLOWING);

    private NPFluids() {}
}
