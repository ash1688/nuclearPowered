package com.ash1688.nuclearpowered.gametest;

import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.IItemHandler;

/** Issue #2 — Hot Spent Fuel Rod vs (cooled) Spent Fuel Rod. Run with `gradlew runGameTestServer`. */
@GameTestHolder(NuclearPowered.MODID)
@PrefixGameTestTemplate(false)
public final class SpentFuelRodTests {
    private static final BlockPos MACHINE = new BlockPos(1, 1, 1);

    @GameTest(template = "empty")
    public static void hotRodHurtsWhenSelected(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        ItemStack rod = new ItemStack(NPItems.HOT_SPENT_FUEL_ROD.get());
        float before = player.getHealth();
        rod.inventoryTick(helper.getLevel(), player, 0, true);
        helper.assertTrue(player.getHealth() < before, "Selected hot rod should damage the holder");
        helper.assertTrue(player.getRemainingFireTicks() > 0, "Selected hot rod should ignite the holder");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void hotRodHarmlessWhenNotSelected(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        ItemStack rod = new ItemStack(NPItems.HOT_SPENT_FUEL_ROD.get());
        float before = player.getHealth();
        rod.inventoryTick(helper.getLevel(), player, 5, false);
        helper.assertTrue(player.getHealth() == before, "Unselected hot rod should not damage");
        helper.assertTrue(player.getRemainingFireTicks() <= 0, "Unselected hot rod should not ignite");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void hotRodNeverSelfCools(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        ItemStack rod = new ItemStack(NPItems.HOT_SPENT_FUEL_ROD.get());
        for (int i = 0; i < 200; i++) rod.inventoryTick(helper.getLevel(), player, 5, false);
        helper.assertTrue(rod.is(NPItems.HOT_SPENT_FUEL_ROD.get()), "Hot rod must not turn into a Spent Fuel Rod on its own");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cutterAcceptsOnlyCooledRods(GameTestHelper helper) {
        helper.setBlock(MACHINE, NPBlocks.CUTTER.get());
        IItemHandler input = helper.getBlockEntity(MACHINE)
                .getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP)
                .orElseThrow(() -> new IllegalStateException("Cutter has no input handler"));

        ItemStack hot = new ItemStack(NPItems.HOT_SPENT_FUEL_ROD.get());
        helper.assertTrue(input.insertItem(0, hot, true).getCount() == 1, "Cutter must reject a Hot Spent Fuel Rod");

        ItemStack cooled = new ItemStack(NPItems.SPENT_FUEL_ROD.get());
        helper.assertTrue(input.insertItem(0, cooled, true).isEmpty(), "Cutter must accept a Spent Fuel Rod");
        helper.succeed();
    }

    private SpentFuelRodTests() {}
}
