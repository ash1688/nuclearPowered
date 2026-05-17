package com.ash1688.nuclearpowered.init;

import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.machine.OreCrusherMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class NPMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, NuclearPowered.MODID);

    public static final RegistryObject<MenuType<OreCrusherMenu>> ORE_CRUSHER =
            REGISTER.register("ore_crusher",
                    () -> IForgeMenuType.create(OreCrusherMenu::fromNetwork));

    private NPMenus() {}
}
