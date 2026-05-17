package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class MaceratorMenu extends BaseMachineMenu {
    public MaceratorMenu(int id, Inventory playerInv, AbstractMachineBlockEntity be) {
        super(NPMenus.MACERATOR.get(), id, playerInv, be, NPBlocks.MACERATOR,
                new int[][]{{56, 35}}, new int[][]{{116, 35}});
    }
    public static MaceratorMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        return new MaceratorMenu(id, playerInv, readBE(buf, playerInv));
    }
}
