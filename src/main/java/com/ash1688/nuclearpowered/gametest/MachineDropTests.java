package com.ash1688.nuclearpowered.gametest;

import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.init.NPBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/** Machines must be pickaxe-mineable and drop themselves, else they vanish when broken. */
@GameTestHolder(NuclearPowered.MODID)
@PrefixGameTestTemplate(false)
public final class MachineDropTests {
    private static final BlockPos POS = new BlockPos(1, 1, 1);

    @GameTest(template = "empty")
    public static void machinesDropThemselvesWithIronPickaxe(GameTestHelper helper) {
        List<RegistryObject<Block>> machines = List.of(
                NPBlocks.ORE_CRUSHER, NPBlocks.CUTTER, NPBlocks.MACERATOR, NPBlocks.SEPARATOR,
                NPBlocks.MIXER, NPBlocks.FUEL_ROD_ASSEMBLY, NPBlocks.GASEOUS_DIFFUSION, NPBlocks.CONVERTER,
                NPBlocks.COAL_BOILER, NPBlocks.STEAM_ENGINE);
        ItemStack pick = new ItemStack(Items.IRON_PICKAXE);
        for (RegistryObject<Block> machine : machines) {
            Block block = machine.get();
            BlockState state = block.defaultBlockState();
            String name = machine.getId().getPath();
            helper.assertTrue(pick.isCorrectToolForDrops(state), name + " is not harvestable with an iron pickaxe");
            List<ItemStack> drops = Block.getDrops(state, helper.getLevel(), helper.absolutePos(POS), null, null, pick);
            helper.assertTrue(drops.stream().anyMatch(s -> s.is(block.asItem())), name + " does not drop itself");
        }
        helper.succeed();
    }
}
