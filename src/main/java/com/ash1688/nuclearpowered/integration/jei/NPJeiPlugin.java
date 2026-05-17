package com.ash1688.nuclearpowered.integration.jei;

import com.ash1688.nuclearpowered.NuclearPowered;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

/**
 * JEI plugin entry point.
 *
 * For M2 this is a no-op scaffold — it registers with JEI but doesn't hide anything yet.
 * In M3 we add hidden-by-default behaviour for our machine recipes (Ore Crusher, Mixer, etc.)
 * and an unhide-on-FTBQ-shop-purchase hook (M9 wiring).
 *
 * Design intent (doc §2.3): items remain craftable from registration; only the RECIPE LISTING
 * in JEI is hidden until the coin shop entry is purchased. We use JEI's runtime API to
 * toggle visibility rather than gating the recipe itself, so creative-mode / commands can
 * still create the items freely.
 */
@JeiPlugin
public final class NPJeiPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_UID =
            new ResourceLocation(NuclearPowered.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // M3: register custom machine recipe categories (Ore Crusher, Centrifuge, etc.)
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        // M3: read FTBQ unlock state and call jeiRuntime.getRecipeManager().hideRecipes(...)
        //     for any recipes whose shop entry has not been purchased yet.
    }
}
