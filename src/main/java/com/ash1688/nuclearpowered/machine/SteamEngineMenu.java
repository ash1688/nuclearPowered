package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public final class SteamEngineMenu extends BaseMachineMenu {
    public SteamEngineMenu(int id, Inventory playerInv, AbstractMachineBlockEntity be) {
        super(NPMenus.STEAM_ENGINE.get(), id, playerInv, be, NPBlocks.STEAM_ENGINE,
                new int[0][0],
                new int[0][0]);
    }
    public static SteamEngineMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        return new SteamEngineMenu(id, playerInv, readBE(buf, playerInv));
    }
}
