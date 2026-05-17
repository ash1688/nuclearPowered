package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class SeparatorMenu extends BaseMachineMenu {
    public SeparatorMenu(int id, Inventory playerInv, AbstractMachineBlockEntity be) {
        super(NPMenus.SEPARATOR.get(), id, playerInv, be, NPBlocks.SEPARATOR,
                new int[][]{{52, 35}},
                new int[][]{{108, 26}, {108, 44}});
    }
    public static SeparatorMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        return new SeparatorMenu(id, playerInv, readBE(buf, playerInv));
    }
}
