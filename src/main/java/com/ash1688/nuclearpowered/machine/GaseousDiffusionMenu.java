package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class GaseousDiffusionMenu extends BaseMachineMenu {
    public GaseousDiffusionMenu(int id, Inventory playerInv, AbstractMachineBlockEntity be) {
        super(NPMenus.GASEOUS_DIFFUSION.get(), id, playerInv, be, NPBlocks.GASEOUS_DIFFUSION,
                new int[][]{{56, 35}},
                new int[0][0]);
    }
    public static GaseousDiffusionMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        return new GaseousDiffusionMenu(id, playerInv, readBE(buf, playerInv));
    }
}
