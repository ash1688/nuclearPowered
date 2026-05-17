package com.ash1688.nuclearpowered.init;

import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.machine.OreCrusherBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class NPBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NuclearPowered.MODID);

    public static final RegistryObject<BlockEntityType<OreCrusherBlockEntity>> ORE_CRUSHER =
            REGISTER.register("ore_crusher",
                    () -> BlockEntityType.Builder.of(OreCrusherBlockEntity::new,
                            NPBlocks.ORE_CRUSHER.get()).build(null));

    private NPBlockEntities() {}
}
