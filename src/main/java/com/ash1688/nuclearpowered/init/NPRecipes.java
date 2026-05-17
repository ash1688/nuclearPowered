package com.ash1688.nuclearpowered.init;

import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.recipe.MachineRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class NPRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.RECIPE_TYPES, NuclearPowered.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, NuclearPowered.MODID);

    public static final RegistryObject<RecipeType<MachineRecipe>> ORE_CRUSHER_TYPE = type("ore_crusher");
    public static final RegistryObject<RecipeType<MachineRecipe>> CUTTER_TYPE = type("cutter");
    public static final RegistryObject<RecipeType<MachineRecipe>> MACERATOR_TYPE = type("macerator");
    public static final RegistryObject<RecipeType<MachineRecipe>> SEPARATOR_TYPE = type("separator");
    public static final RegistryObject<RecipeType<MachineRecipe>> MIXER_TYPE = type("mixer");
    public static final RegistryObject<RecipeType<MachineRecipe>> FUEL_ROD_ASSEMBLY_TYPE = type("fuel_rod_assembly");
    public static final RegistryObject<RecipeType<MachineRecipe>> GASEOUS_DIFFUSION_TYPE = type("gaseous_diffusion");
    public static final RegistryObject<RecipeType<MachineRecipe>> CENTRIFUGE_TYPE = type("centrifuge");
    public static final RegistryObject<RecipeType<MachineRecipe>> CONVERTER_TYPE = type("converter");

    public static final RegistryObject<RecipeSerializer<?>> ORE_CRUSHER_SERIALIZER = serializer("ore_crusher", ORE_CRUSHER_TYPE);
    public static final RegistryObject<RecipeSerializer<?>> CUTTER_SERIALIZER = serializer("cutter", CUTTER_TYPE);
    public static final RegistryObject<RecipeSerializer<?>> MACERATOR_SERIALIZER = serializer("macerator", MACERATOR_TYPE);
    public static final RegistryObject<RecipeSerializer<?>> SEPARATOR_SERIALIZER = serializer("separator", SEPARATOR_TYPE);
    public static final RegistryObject<RecipeSerializer<?>> MIXER_SERIALIZER = serializer("mixer", MIXER_TYPE);
    public static final RegistryObject<RecipeSerializer<?>> FUEL_ROD_ASSEMBLY_SERIALIZER = serializer("fuel_rod_assembly", FUEL_ROD_ASSEMBLY_TYPE);
    public static final RegistryObject<RecipeSerializer<?>> GASEOUS_DIFFUSION_SERIALIZER = serializer("gaseous_diffusion", GASEOUS_DIFFUSION_TYPE);
    public static final RegistryObject<RecipeSerializer<?>> CENTRIFUGE_SERIALIZER = serializer("centrifuge", CENTRIFUGE_TYPE);
    public static final RegistryObject<RecipeSerializer<?>> CONVERTER_SERIALIZER = serializer("converter", CONVERTER_TYPE);

    private static RegistryObject<RecipeType<MachineRecipe>> type(String name) {
        return TYPES.register(name, () -> new RecipeType<MachineRecipe>() {
            @Override public String toString() { return new ResourceLocation(NuclearPowered.MODID, name).toString(); }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static RegistryObject<RecipeSerializer<?>> serializer(String name, RegistryObject<RecipeType<MachineRecipe>> typeRef) {
        RegistryObject ro = SERIALIZERS.register(name, () -> new MachineRecipe.Serializer(typeRef::get));
        return (RegistryObject<RecipeSerializer<?>>) ro;
    }

    private NPRecipes() {}
}
