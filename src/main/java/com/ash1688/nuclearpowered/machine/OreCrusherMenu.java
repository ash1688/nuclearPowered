package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlocks;
import com.ash1688.nuclearpowered.init.NPMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public final class OreCrusherMenu extends AbstractContainerMenu {
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int MACHINE_SLOT_COUNT = 2;

    private final OreCrusherBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    /** Server-side ctor (from BE.createMenu). */
    public OreCrusherMenu(int id, Inventory playerInv, OreCrusherBlockEntity be) {
        super(NPMenus.ORE_CRUSHER.get(), id);
        this.blockEntity = be;
        this.access = ContainerLevelAccess.create(be.getLevel(), be.getBlockPos());

        // Machine slots
        addSlot(new SlotItemHandler(be.getInventory(), INPUT_SLOT, 56, 35));
        addSlot(new SlotItemHandler(be.getInventory(), OUTPUT_SLOT, 116, 35) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });

        // Player inventory (3 rows x 9 cols)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }

        // Data sync (progress, max progress, energy, max energy)
        this.data = new ContainerData() {
            @Override public int get(int index) {
                return switch (index) {
                    case 0 -> blockEntity.getProgress();
                    case 1 -> blockEntity.getMaxProgress();
                    case 2 -> blockEntity.getEnergyStored();
                    case 3 -> blockEntity.getEnergyCapacity();
                    default -> 0;
                };
            }
            @Override public void set(int index, int value) { /* server-authoritative */ }
            @Override public int getCount() { return 4; }
        };
        addDataSlots(data);
    }

    /** Client-side ctor (decoded from network). */
    public static OreCrusherMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        var be = playerInv.player.level().getBlockEntity(pos);
        if (!(be instanceof OreCrusherBlockEntity crusher)) {
            throw new IllegalStateException("Block entity at " + pos + " is not an OreCrusherBlockEntity");
        }
        return new OreCrusherMenu(id, playerInv, crusher);
    }

    public int progress() { return data.get(0); }
    public int maxProgress() { return data.get(1); }
    public int energy() { return data.get(2); }
    public int maxEnergy() { return data.get(3); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();

        if (index < MACHINE_SLOT_COUNT) {
            // From machine -> player inv
            if (!moveItemStackTo(original, MACHINE_SLOT_COUNT, MACHINE_SLOT_COUNT + 36, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // From player inv -> machine input slot (output slot disallows insertion)
            if (!moveItemStackTo(original, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        }
        if (original.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, NPBlocks.ORE_CRUSHER.get());
    }
}
