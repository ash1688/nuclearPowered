package com.ash1688.nuclearpowered.recipe;

import com.ash1688.nuclearpowered.init.NPRecipes;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * Ore Crusher recipe: one input → one output, costs FE energy per tick over a duration.
 *
 * JSON schema:
 * {
 *   "type": "nuclearpowered:ore_crusher",
 *   "ingredient": {"item": "..."} or {"tag": "..."},
 *   "result": {"item": "...", "count": 1},
 *   "energy": 800,         // total FE consumed
 *   "duration": 100        // ticks (default 100 = 5s)
 * }
 */
public final class OreCrusherRecipe implements Recipe<SimpleContainer> {
    public static final int DEFAULT_ENERGY = 800;
    public static final int DEFAULT_DURATION = 100;

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final int energy;
    private final int duration;

    public OreCrusherRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, int energy, int duration) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.energy = energy;
        this.duration = duration;
    }

    public Ingredient ingredient() { return ingredient; }
    public int energy() { return energy; }
    public int duration() { return duration; }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        return ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) { return result.copy(); }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public RecipeSerializer<?> getSerializer() { return NPRecipes.ORE_CRUSHER_SERIALIZER.get(); }

    @Override
    public RecipeType<?> getType() { return NPRecipes.ORE_CRUSHER_TYPE.get(); }

    public static final class Serializer implements RecipeSerializer<OreCrusherRecipe> {
        @Override
        public OreCrusherRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ing = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            JsonObject resJson = GsonHelper.getAsJsonObject(json, "result");
            ItemStack result = new ItemStack(
                    net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(
                            new ResourceLocation(GsonHelper.getAsString(resJson, "item"))),
                    GsonHelper.getAsInt(resJson, "count", 1));
            int energy = GsonHelper.getAsInt(json, "energy", DEFAULT_ENERGY);
            int duration = GsonHelper.getAsInt(json, "duration", DEFAULT_DURATION);
            return new OreCrusherRecipe(id, ing, result, energy, duration);
        }

        @Override
        public OreCrusherRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient ing = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            int energy = buf.readVarInt();
            int duration = buf.readVarInt();
            return new OreCrusherRecipe(id, ing, result, energy, duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, OreCrusherRecipe recipe) {
            recipe.ingredient.toNetwork(buf);
            buf.writeItem(recipe.result);
            buf.writeVarInt(recipe.energy);
            buf.writeVarInt(recipe.duration);
        }
    }
}
