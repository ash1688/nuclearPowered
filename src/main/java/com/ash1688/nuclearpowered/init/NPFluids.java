package com.ash1688.nuclearpowered.init;

import com.ash1688.nuclearpowered.NuclearPowered;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Fluid registrations for Nuclear Powered.
 *
 * MVP fluid set:
 *  - UF6 (Hex) - product of gaseous diffusion / centrifuge, input to Converter
 *  - Steam - intermediate between reactor and turbogenerator (water-cooled loops)
 *  - Tritium (S2 byproduct fluid, S3 igniter ingredient) - registered for forward-compat
 *
 * S3+ coolants (heavy water, CO2 gas, molten salt, liquid sodium) are deferred per design doc.
 *
 * Note: For MVP we register the FluidType + still/flowing Fluid placeholders. Block and Bucket
 * registrations are added in M3 alongside the machines that produce/consume these fluids.
 */
public final class NPFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, NuclearPowered.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, NuclearPowered.MODID);

    public static final RegistryObject<FluidType> UF6_TYPE =
            FLUID_TYPES.register("uf6", () -> new FluidType(
                    FluidType.Properties.create()
                            .descriptionId("fluid_type.nuclearpowered.uf6")
                            .density(11000)
                            .viscosity(800)
                            .temperature(330)));

    public static final RegistryObject<FluidType> STEAM_TYPE =
            FLUID_TYPES.register("steam", () -> new FluidType(
                    FluidType.Properties.create()
                            .descriptionId("fluid_type.nuclearpowered.steam")
                            .density(-500)
                            .viscosity(200)
                            .temperature(550)
                            .lightLevel(1)));

    public static final RegistryObject<FluidType> TRITIUM_TYPE =
            FLUID_TYPES.register("tritium", () -> new FluidType(
                    FluidType.Properties.create()
                            .descriptionId("fluid_type.nuclearpowered.tritium")
                            .density(70)
                            .viscosity(400)
                            .temperature(295)
                            .lightLevel(2)));

    private NPFluids() {}
}
