package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class CentrifugeMenu extends BaseMachineMenu {
    public CentrifugeMenu(int id, Inventory playerInv, AbstractMachineBlockEntity be) {
        super(NPMenus.CENTRIFUGE.get(), id, playerInv, be, NPBlocks.CENTRIFUGE,
                new int[][]{{56, 35}},
                new int[0][0]);
    }
    public static CentrifugeMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        return new CentrifugeMenu(id, playerInv, readBE(buf, playerInv));
    }
}
