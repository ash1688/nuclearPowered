package com.ash1688.nuclearpowered.init;

import com.ash1688.nuclearpowered.NuclearPowered;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.core.registries.Registries;

public final class NPCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NuclearPowered.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = REGISTER.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.nuclearpowered"))
                    .icon(() -> new ItemStack(NPItems.NATURAL_URANIUM_FUEL_ROD.get()))
                    .displayItems((params, output) -> {
                        // Items in display order (mirrors design doc §6 sequence)
                        output.accept(NPBlocks.URANIUM_ORE.get());
                        output.accept(NPBlocks.DEEPSLATE_URANIUM_ORE.get());
                        output.accept(NPBlocks.RAW_URANIUM_BLOCK.get());

                        output.accept(NPItems.RAW_URANIUM.get());
                        output.accept(NPItems.YELLOWCAKE.get());
                        output.accept(NPItems.URANIUM_OXIDE.get());
                        output.accept(NPItems.GREEN_SALT.get());

                        output.accept(NPItems.RECLAIM_URANIUM_POWDER.get());
                        output.accept(NPItems.PLUTONIUM_DUST.get());
                        output.accept(NPItems.CAESIUM.get());

                        output.accept(NPItems.NATURAL_URANIUM_PELLET.get());
                        output.accept(NPItems.LEU_PELLET.get());
                        output.accept(NPItems.MOX_PELLET.get());

                        output.accept(NPItems.EMPTY_CLADDING.get());
                        output.accept(NPItems.NATURAL_URANIUM_FUEL_ROD.get());
                        output.accept(NPItems.LEU_FUEL_ROD.get());
                        output.accept(NPItems.MOX_FUEL_ROD.get());
                        output.accept(NPItems.SPENT_FUEL_ROD.get());

                        output.accept(NPItems.IGNITER_S2.get());

                        output.accept(NPItems.STAGE_1_COIN.get());
                        output.accept(NPItems.STAGE_2_COIN.get());

                        output.accept(NPItems.UPGRADE_EXTENDED_BURN.get());
                        output.accept(NPItems.UPGRADE_REDUCED_WATER.get());
                        output.accept(NPItems.UPGRADE_INCREASED_STEAM.get());
                        output.accept(NPItems.UPGRADE_CLADDING.get());

                        // Machines
                        output.accept(NPBlocks.ORE_CRUSHER.get());
                    })
                    .build());

    private NPCreativeTabs() {}
}
