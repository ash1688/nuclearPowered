package com.ash1688.nuclearpowered.init;

import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.recipe.OreCrusherRecipe;
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

    public static final RegistryObject<RecipeType<OreCrusherRecipe>> ORE_CRUSHER_TYPE =
            TYPES.register("ore_crusher", () -> new RecipeType<OreCrusherRecipe>() {
                @Override public String toString() { return new ResourceLocation(NuclearPowered.MODID, "ore_crusher").toString(); }
            });

    public static final RegistryObject<RecipeSerializer<OreCrusherRecipe>> ORE_CRUSHER_SERIALIZER =
            SERIALIZERS.register("ore_crusher", OreCrusherRecipe.Serializer::new);

    private NPRecipes() {}
}
