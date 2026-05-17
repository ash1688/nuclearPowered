package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class MixerMenu extends BaseMachineMenu {
    public MixerMenu(int id, Inventory playerInv, AbstractMachineBlockEntity be) {
        super(NPMenus.MIXER.get(), id, playerInv, be, NPBlocks.MIXER,
                new int[][]{{38, 35}, {76, 35}},
                new int[][]{{130, 35}});
    }
    public static MixerMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        return new MixerMenu(id, playerInv, readBE(buf, playerInv));
    }
}
