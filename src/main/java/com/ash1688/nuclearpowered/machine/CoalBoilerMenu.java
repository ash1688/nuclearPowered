package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class CoalBoilerMenu extends BaseMachineMenu {
    public CoalBoilerMenu(int id, Inventory playerInv, AbstractMachineBlockEntity be) {
        super(NPMenus.COAL_BOILER.get(), id, playerInv, be, NPBlocks.COAL_BOILER,
                new int[][]{{56, 53}},
                new int[0][0]);
    }
    public static CoalBoilerMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        return new CoalBoilerMenu(id, playerInv, readBE(buf, playerInv));
    }
}
