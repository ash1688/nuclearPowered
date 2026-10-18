package com.ash1688.nuclearpowered.gametest;

import com.ash1688.nuclearpowered.NPConfig;
import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPFluids;
import com.ash1688.nuclearpowered.init.NPItems;
import com.ash1688.nuclearpowered.machine.AbstractMachineBlockEntity;
import com.ash1688.nuclearpowered.machine.CoalBoilerBlockEntity;
import com.ash1688.nuclearpowered.machine.SteamEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/** PRD #21 — Bootstrap tier (Coal Boiler + Steam Engine). Run with `gradlew runGameTestServer`. */
@GameTestHolder(NuclearPowered.MODID)
@PrefixGameTestTemplate(false)
public final class BootstrapPowerTests {
    private static final BlockPos BOILER = new BlockPos(0, 1, 1);
    private static final BlockPos ENGINE = new BlockPos(1, 1, 1);
    private static final BlockPos CRUSHER = new BlockPos(2, 1, 1);

    private static <T> T be(GameTestHelper helper, BlockPos pos, Class<T> type) {
        return type.cast(helper.getBlockEntity(pos));
    }

    private static void fillSteam(SteamEngineBlockEntity engine, int mb) {
        engine.getInputFluidTank().fill(new FluidStack(NPFluids.STEAM.get(), mb), IFluidHandler.FluidAction.EXECUTE);
    }

    @GameTest(template = "empty")
    public static void engineFillsNeighbourButNeverPastCap(GameTestHelper helper) {
        helper.setBlock(ENGINE, NPBlocks.STEAM_ENGINE.get());
        helper.setBlock(CRUSHER, NPBlocks.ORE_CRUSHER.get());
        fillSteam(be(helper, ENGINE, SteamEngineBlockEntity.class), 8000);
        int cap = NPConfig.COMMON.steamEngineMaxFePerTick.get();
        helper.runAtTickTime(10, () -> {
            int fe = be(helper, CRUSHER, AbstractMachineBlockEntity.class).getEnergyStored();
            helper.assertTrue(fe > 0, "Engine should push FE into the adjacent Ore Crusher");
            helper.assertTrue(fe <= 10 * cap, "Engine pushed " + fe + " FE in 10 ticks, above the " + cap + " FE/t cap");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 400)
    public static void coalPowersAnOreCrusher(GameTestHelper helper) {
        helper.setBlock(BOILER, NPBlocks.COAL_BOILER.get());
        helper.setBlock(ENGINE, NPBlocks.STEAM_ENGINE.get());
        helper.setBlock(CRUSHER, NPBlocks.ORE_CRUSHER.get());
        CoalBoilerBlockEntity boiler = be(helper, BOILER, CoalBoilerBlockEntity.class);
        boiler.getInventory().insertItem(0, new ItemStack(Items.COAL), false);
        boiler.getInputFluidTank().fill(new FluidStack(Fluids.WATER, 8000), IFluidHandler.FluidAction.EXECUTE);
        AbstractMachineBlockEntity crusher = be(helper, CRUSHER, AbstractMachineBlockEntity.class);
        crusher.getInventory().insertItem(0, new ItemStack(NPItems.RAW_URANIUM.get()), false);

        helper.succeedWhen(() -> helper.assertTrue(
                crusher.getInventory().getStackInSlot(1).is(NPItems.YELLOWCAKE.get()),
                "Ore Crusher should finish a recipe on coal power"));
    }

    @GameTest(template = "empty")
    public static void boilerKeepsCoalWithoutWater(GameTestHelper helper) {
        helper.setBlock(BOILER, NPBlocks.COAL_BOILER.get());
        CoalBoilerBlockEntity boiler = be(helper, BOILER, CoalBoilerBlockEntity.class);
        boiler.getInventory().insertItem(0, new ItemStack(Items.COAL), false);
        helper.runAtTickTime(40, () -> {
            helper.assertTrue(boiler.getInventory().getStackInSlot(0).getCount() == 1, "Coal must not be burnt without water");
            helper.assertTrue(boiler.getProgress() == 0, "Burn must not start without water");
            helper.succeed();
        });
    }

    @GameTest(template = "empty")
    public static void boilerPausesWhenSteamTankIsFull(GameTestHelper helper) {
        helper.setBlock(BOILER, NPBlocks.COAL_BOILER.get());
        CoalBoilerBlockEntity boiler = be(helper, BOILER, CoalBoilerBlockEntity.class);
        boiler.getInventory().insertItem(0, new ItemStack(Items.COAL), false);
        boiler.getInputFluidTank().fill(new FluidStack(Fluids.WATER, 8000), IFluidHandler.FluidAction.EXECUTE);
        boiler.getOutputFluidTank().fill(new FluidStack(NPFluids.STEAM.get(), Integer.MAX_VALUE), IFluidHandler.FluidAction.EXECUTE);
        int water = boiler.getInputFluidTank().getFluidAmount();
        helper.runAtTickTime(40, () -> {
            helper.assertTrue(boiler.getInventory().getStackInSlot(0).getCount() == 1, "Coal must not be burnt into a full steam tank");
            helper.assertTrue(boiler.getInputFluidTank().getFluidAmount() == water, "Water must not be used into a full steam tank");
            helper.succeed();
        });
    }

    @GameTest(template = "empty")
    public static void engineIdlesWithoutSteam(GameTestHelper helper) {
        helper.setBlock(ENGINE, NPBlocks.STEAM_ENGINE.get());
        helper.runAtTickTime(20, () -> {
            helper.assertTrue(be(helper, ENGINE, SteamEngineBlockEntity.class).getEnergyStored() == 0, "Engine without steam must make no FE");
            helper.succeed();
        });
    }

    @GameTest(template = "empty")
    public static void engineRejectsNonSteamFluids(GameTestHelper helper) {
        helper.setBlock(ENGINE, NPBlocks.STEAM_ENGINE.get());
        IFluidHandler in = helper.getBlockEntity(ENGINE)
                .getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.UP)
                .orElseThrow(() -> new IllegalStateException("Engine has no fluid input"));
        helper.assertTrue(in.fill(new FluidStack(Fluids.LAVA, 1000), IFluidHandler.FluidAction.SIMULATE) == 0, "Engine must reject lava");
        helper.assertTrue(in.fill(new FluidStack(NPFluids.STEAM.get(), 1000), IFluidHandler.FluidAction.SIMULATE) == 1000, "Engine must accept steam");
        helper.succeed();
    }

    private BootstrapPowerTests() {}
}
