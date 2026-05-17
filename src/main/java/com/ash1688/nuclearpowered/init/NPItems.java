package com.ash1688.nuclearpowered.init;

import com.ash1688.nuclearpowered.NuclearPowered;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Item registrations for Nuclear Powered.
 *
 * MVP scope (S1 + S2):
 *  - Ores & raw materials: Yellowcake, UO2, UF4 (UF6 is a fluid; see NPFluids)
 *  - Fuel pellets: Natural U, LEU, MOX
 *  - Cladding (empty), finished fuel rods (Natural U, LEU, MOX), spent fuel rods
 *  - Byproducts: Plutonium Dust, Caesium (Cs), Reclaim Uranium Powder
 *  - Igniter (S2): cold-start item for S2 reactors
 *  - Coins: Stage 1 Coin, Stage 2 Coin
 *  - Upgrade items: Extended Burn / Reduced Water / Increased Steam controller upgrades, Cladding Upgrade
 *
 * Stages 3-5 fuels and igniters are intentionally deferred to post-MVP per design doc v1.1.
 */
public final class NPItems {
    public static final DeferredRegister<Item> REGISTER =
            DeferredRegister.create(ForgeRegistries.ITEMS, NuclearPowered.MODID);

    private static Item.Properties basic() { return new Item.Properties(); }

    // ===== Ores & raw materials =====
    public static final RegistryObject<Item> RAW_URANIUM = REGISTER.register("raw_uranium", () -> new Item(basic()));
    public static final RegistryObject<Item> YELLOWCAKE = REGISTER.register("yellowcake", () -> new Item(basic()));
    public static final RegistryObject<Item> URANIUM_OXIDE = REGISTER.register("uranium_oxide", () -> new Item(basic()));
    public static final RegistryObject<Item> GREEN_SALT = REGISTER.register("green_salt", () -> new Item(basic())); // UF4

    // ===== Byproducts =====
    public static final RegistryObject<Item> PLUTONIUM_DUST = REGISTER.register("plutonium_dust", () -> new Item(basic()));
    public static final RegistryObject<Item> CAESIUM = REGISTER.register("caesium", () -> new Item(basic()));
    public static final RegistryObject<Item> RECLAIM_URANIUM_POWDER = REGISTER.register("reclaim_uranium_powder", () -> new Item(basic()));

    // ===== Fuel pellets =====
    public static final RegistryObject<Item> NATURAL_URANIUM_PELLET = REGISTER.register("natural_uranium_pellet", () -> new Item(basic()));
    public static final RegistryObject<Item> LEU_PELLET = REGISTER.register("leu_pellet", () -> new Item(basic()));
    public static final RegistryObject<Item> MOX_PELLET = REGISTER.register("mox_pellet", () -> new Item(basic()));

    // ===== Cladding & fuel rods =====
    public static final RegistryObject<Item> EMPTY_CLADDING = REGISTER.register("empty_cladding", () -> new Item(basic()));
    public static final RegistryObject<Item> NATURAL_URANIUM_FUEL_ROD = REGISTER.register("natural_uranium_fuel_rod", () -> new Item(basic()));
    public static final RegistryObject<Item> LEU_FUEL_ROD = REGISTER.register("leu_fuel_rod", () -> new Item(basic()));
    public static final RegistryObject<Item> MOX_FUEL_ROD = REGISTER.register("mox_fuel_rod", () -> new Item(basic()));
    public static final RegistryObject<Item> SPENT_FUEL_ROD = REGISTER.register("spent_fuel_rod", () -> new Item(basic()));

    // ===== Igniter (S2 cold-start) =====
    public static final RegistryObject<Item> IGNITER_S2 = REGISTER.register("igniter_s2", () -> new Item(basic().stacksTo(16)));

    // ===== Coins =====
    public static final RegistryObject<Item> STAGE_1_COIN = REGISTER.register("stage_1_coin", () -> new Item(basic().stacksTo(64)));
    public static final RegistryObject<Item> STAGE_2_COIN = REGISTER.register("stage_2_coin", () -> new Item(basic().stacksTo(64)));

    // ===== Controller upgrades =====
    public static final RegistryObject<Item> UPGRADE_EXTENDED_BURN = REGISTER.register("upgrade_extended_burn", () -> new Item(basic().stacksTo(1)));
    public static final RegistryObject<Item> UPGRADE_REDUCED_WATER = REGISTER.register("upgrade_reduced_water", () -> new Item(basic().stacksTo(1)));
    public static final RegistryObject<Item> UPGRADE_INCREASED_STEAM = REGISTER.register("upgrade_increased_steam", () -> new Item(basic().stacksTo(1)));
    public static final RegistryObject<Item> UPGRADE_CLADDING = REGISTER.register("upgrade_cladding", () -> new Item(basic()));

    private NPItems() {}
}
