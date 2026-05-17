package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class FuelRodAssemblyMenu extends BaseMachineMenu {
    public FuelRodAssemblyMenu(int id, Inventory playerInv, AbstractMachineBlockEntity be) {
        super(NPMenus.FUEL_ROD_ASSEMBLY.get(), id, playerInv, be, NPBlocks.FUEL_ROD_ASSEMBLY,
                new int[][]{{38, 35}, {76, 35}},
                new int[][]{{130, 35}});
    }
    public static FuelRodAssemblyMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        return new FuelRodAssemblyMenu(id, playerInv, readBE(buf, playerInv));
    }
}
