package com.ash1688.nuclearpowered.init;

import com.ash1688.nuclearpowered.NuclearPowered;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Block registrations for Nuclear Powered (MVP scaffolding).
 *
 * For M1 only the world-gen ore is registered. Reactor casing, control rod, fuel rod block,
 * moderator, thermocouple, controller, ports, ejector, and machine blocks are added in
 * later milestones (M4-M6). Keeping this file small now avoids dead registrations.
 */
public final class NPBlocks {
    public static final DeferredRegister<Block> REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCKS, NuclearPowered.MODID);

    public static final RegistryObject<Block> URANIUM_ORE = registerWithItem(
            "uranium_ore",
            () -> new DropExperienceBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .strength(3.0F, 3.0F)
                            .requiresCorrectToolForDrops(),
                    UniformInt.of(2, 5))
    );

    public static final RegistryObject<Block> DEEPSLATE_URANIUM_ORE = registerWithItem(
            "deepslate_uranium_ore",
            () -> new DropExperienceBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DEEPSLATE)
                            .strength(4.5F, 3.0F)
                            .requiresCorrectToolForDrops(),
                    UniformInt.of(2, 5))
    );

    public static final RegistryObject<Block> RAW_URANIUM_BLOCK = registerWithItem(
            "raw_uranium_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(5.0F, 6.0F)
                    .requiresCorrectToolForDrops())
    );

    private static <B extends Block> RegistryObject<B> registerWithItem(String name, Supplier<B> blockSupplier) {
        RegistryObject<B> block = REGISTER.register(name, blockSupplier);
        NPItems.REGISTER.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private NPBlocks() {}
}
