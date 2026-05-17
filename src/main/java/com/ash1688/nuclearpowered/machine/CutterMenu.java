package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class CutterMenu extends BaseMachineMenu {
    public CutterMenu(int id, Inventory playerInv, AbstractMachineBlockEntity be) {
        super(NPMenus.CUTTER.get(), id, playerInv, be, NPBlocks.CUTTER,
                new int[][]{{56, 35}}, new int[][]{{116, 35}});
    }
    public static CutterMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        return new CutterMenu(id, playerInv, readBE(buf, playerInv));
    }
}
