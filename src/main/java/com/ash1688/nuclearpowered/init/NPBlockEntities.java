package com.ash1688.nuclearpowered.init;

import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.machine.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class NPBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NuclearPowered.MODID);

    public static final RegistryObject<BlockEntityType<OreCrusherBlockEntity>> ORE_CRUSHER =
            REGISTER.register("ore_crusher", () -> BlockEntityType.Builder.of(OreCrusherBlockEntity::new, NPBlocks.ORE_CRUSHER.get()).build(null));
    public static final RegistryObject<BlockEntityType<CutterBlockEntity>> CUTTER =
            REGISTER.register("cutter", () -> BlockEntityType.Builder.of(CutterBlockEntity::new, NPBlocks.CUTTER.get()).build(null));
    public static final RegistryObject<BlockEntityType<MaceratorBlockEntity>> MACERATOR =
            REGISTER.register("macerator", () -> BlockEntityType.Builder.of(MaceratorBlockEntity::new, NPBlocks.MACERATOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<SeparatorBlockEntity>> SEPARATOR =
            REGISTER.register("separator", () -> BlockEntityType.Builder.of(SeparatorBlockEntity::new, NPBlocks.SEPARATOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<MixerBlockEntity>> MIXER =
            REGISTER.register("mixer", () -> BlockEntityType.Builder.of(MixerBlockEntity::new, NPBlocks.MIXER.get()).build(null));
    public static final RegistryObject<BlockEntityType<FuelRodAssemblyBlockEntity>> FUEL_ROD_ASSEMBLY =
            REGISTER.register("fuel_rod_assembly", () -> BlockEntityType.Builder.of(FuelRodAssemblyBlockEntity::new, NPBlocks.FUEL_ROD_ASSEMBLY.get()).build(null));
    public static final RegistryObject<BlockEntityType<GaseousDiffusionBlockEntity>> GASEOUS_DIFFUSION =
            REGISTER.register("gaseous_diffusion", () -> BlockEntityType.Builder.of(GaseousDiffusionBlockEntity::new, NPBlocks.GASEOUS_DIFFUSION.get()).build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeBlockEntity>> CENTRIFUGE =
            REGISTER.register("centrifuge", () -> BlockEntityType.Builder.of(CentrifugeBlockEntity::new, NPBlocks.CENTRIFUGE.get()).build(null));
    public static final RegistryObject<BlockEntityType<ConverterBlockEntity>> CONVERTER =
            REGISTER.register("converter", () -> BlockEntityType.Builder.of(ConverterBlockEntity::new, NPBlocks.CONVERTER.get()).build(null));

    private NPBlockEntities() {}
}
