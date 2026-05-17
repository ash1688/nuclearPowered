package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class OreCrusherMenu extends BaseMachineMenu {
    public OreCrusherMenu(int id, Inventory playerInv, AbstractMachineBlockEntity be) {
        super(NPMenus.ORE_CRUSHER.get(), id, playerInv, be, NPBlocks.ORE_CRUSHER,
                new int[][]{{56, 35}},
                new int[][]{{116, 35}});
    }
    public static OreCrusherMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        return new OreCrusherMenu(id, playerInv, readBE(buf, playerInv));
    }
}
