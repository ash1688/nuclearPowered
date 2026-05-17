package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class ConverterMenu extends BaseMachineMenu {
    public ConverterMenu(int id, Inventory playerInv, AbstractMachineBlockEntity be) {
        super(NPMenus.CONVERTER.get(), id, playerInv, be, NPBlocks.CONVERTER,
                new int[0][0],
                new int[][]{{116, 35}});
    }
    public static ConverterMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        return new ConverterMenu(id, playerInv, readBE(buf, playerInv));
    }
}
