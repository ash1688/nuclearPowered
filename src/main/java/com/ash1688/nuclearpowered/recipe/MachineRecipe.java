package com.ash1688.nuclearpowered.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Generic single-block-machine recipe. Used for every M3 machine.
 *
 * JSON schema:
 * {
 *   "type": "nuclearpowered:<machine>",
 *   "ingredients": [{...}, {...}],         // 1-N item ingredients
 *   "input_fluid": {"fluid": "...", "amount": N},   // optional
 *   "result": {"item": "...", "count": N},          // main item output (may be empty if fluid-only)
 *   "secondary_result": {"item": "...", "count": N, "chance": 100},  // optional
 *   "output_fluid": {"fluid": "...", "amount": N},  // optional
 *   "energy": 1000,                                  // total FE consumed
 *   "duration": 100                                  // ticks
 * }
 *
 * Each machine declares its own RecipeType + RecipeSerializer in NPRecipes. The
 * serializer holds a reference to its RecipeType so getType() returns the right one.
 */
public class MachineRecipe implements Recipe<SimpleContainer> {
    public static final FluidStack EMPTY_FLUID = FluidStack.EMPTY;

    protected final ResourceLocation id;
    protected final NonNullList<Ingredient> ingredients;
    protected final FluidStack inputFluid;
    protected final ItemStack result;
    protected final ItemStack secondaryResult;
    protected final int secondaryChance;        // 0-100
    protected final FluidStack outputFluid;
    protected final int energy;
    protected final int duration;
    protected final Supplier<RecipeType<?>> typeRef;
    protected final Supplier<RecipeSerializer<?>> serializerRef;

    public MachineRecipe(ResourceLocation id,
                         NonNullList<Ingredient> ingredients, FluidStack inputFluid,
                         ItemStack result, ItemStack secondaryResult, int secondaryChance,
                         FluidStack outputFluid, int energy, int duration,
                         Supplier<RecipeType<?>> typeRef, Supplier<RecipeSerializer<?>> serializerRef) {
        this.id = id;
        this.ingredients = ingredients;
        this.inputFluid = inputFluid;
        this.result = result;
        this.secondaryResult = secondaryResult;
        this.secondaryChance = secondaryChance;
        this.outputFluid = outputFluid;
        this.energy = energy;
        this.duration = duration;
        this.typeRef = typeRef;
        this.serializerRef = serializerRef;
    }

    public NonNullList<Ingredient> ingredients() { return ingredients; }
    public FluidStack inputFluid() { return inputFluid; }
    public ItemStack mainResult() { return result; }
    public ItemStack secondaryResult() { return secondaryResult; }
    public int secondaryChance() { return secondaryChance; }
    public FluidStack outputFluid() { return outputFluid; }
    public int energy() { return energy; }
    public int duration() { return duration; }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if (container.getContainerSize() < ingredients.size()) return false;
        // Each ingredient must match by-position. (Multi-input machines have a fixed slot order.)
        for (int i = 0; i < ingredients.size(); i++) {
            if (!ingredients.get(i).test(container.getItem(i))) return false;
        }
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer container, @NotNull RegistryAccess registries) { return result.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registries) { return result.copy(); }
    @Override public @NotNull NonNullList<Ingredient> getIngredients() { return ingredients; }
    @Override public @NotNull ResourceLocation getId() { return id; }
    @Override public @NotNull RecipeSerializer<?> getSerializer() { return serializerRef.get(); }
    @Override public @NotNull RecipeType<?> getType() { return typeRef.get(); }

    // ===== Serializer =====

    public static final class Serializer implements RecipeSerializer<MachineRecipe> {
        private final Supplier<RecipeType<?>> typeRef;
        private final Supplier<RecipeSerializer<?>> selfRef = () -> this;

        public Serializer(Supplier<RecipeType<?>> typeRef) {
            this.typeRef = typeRef;
        }

        @Override
        public @NotNull MachineRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
            JsonArray ingArr = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ings = NonNullList.create();
            for (JsonElement el : ingArr) ings.add(Ingredient.fromJson(el));

            FluidStack inFluid = readOptionalFluid(json, "input_fluid");
            ItemStack result = json.has("result") ? readItem(json.getAsJsonObject("result")) : ItemStack.EMPTY;
            ItemStack secondary = json.has("secondary_result") ? readItem(json.getAsJsonObject("secondary_result")) : ItemStack.EMPTY;
            int secondaryChance = json.has("secondary_result")
                    ? GsonHelper.getAsInt(json.getAsJsonObject("secondary_result"), "chance", 100)
                    : 0;
            FluidStack outFluid = readOptionalFluid(json, "output_fluid");
            int energy = GsonHelper.getAsInt(json, "energy", 800);
            int duration = GsonHelper.getAsInt(json, "duration", 100);

            return new MachineRecipe(id, ings, inFluid, result, secondary, secondaryChance,
                    outFluid, energy, duration, typeRef, selfRef);
        }

        @Override
        public @NotNull MachineRecipe fromNetwork(@NotNull ResourceLocation id, FriendlyByteBuf buf) {
            int ingCount = buf.readVarInt();
            NonNullList<Ingredient> ings = NonNullList.withSize(ingCount, Ingredient.EMPTY);
            for (int i = 0; i < ingCount; i++) ings.set(i, Ingredient.fromNetwork(buf));
            FluidStack inFluid = buf.readFluidStack();
            ItemStack result = buf.readItem();
            ItemStack secondary = buf.readItem();
            int secondaryChance = buf.readVarInt();
            FluidStack outFluid = buf.readFluidStack();
            int energy = buf.readVarInt();
            int duration = buf.readVarInt();
            return new MachineRecipe(id, ings, inFluid, result, secondary, secondaryChance,
                    outFluid, energy, duration, typeRef, selfRef);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, MachineRecipe r) {
            buf.writeVarInt(r.ingredients.size());
            for (Ingredient i : r.ingredients) i.toNetwork(buf);
            buf.writeFluidStack(r.inputFluid);
            buf.writeItem(r.result);
            buf.writeItem(r.secondaryResult);
            buf.writeVarInt(r.secondaryChance);
            buf.writeFluidStack(r.outputFluid);
            buf.writeVarInt(r.energy);
            buf.writeVarInt(r.duration);
        }

        private static ItemStack readItem(JsonObject obj) {
            ItemStack s = new ItemStack(ForgeRegistries.ITEMS.getValue(
                    new ResourceLocation(GsonHelper.getAsString(obj, "item"))),
                    GsonHelper.getAsInt(obj, "count", 1));
            return s;
        }

        private static FluidStack readOptionalFluid(JsonObject json, String key) {
            if (!json.has(key)) return FluidStack.EMPTY;
            JsonObject obj = json.getAsJsonObject(key);
            var fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(obj, "fluid")));
            int amount = GsonHelper.getAsInt(obj, "amount", 1000);
            return new FluidStack(fluid, amount);
        }
    }
}
