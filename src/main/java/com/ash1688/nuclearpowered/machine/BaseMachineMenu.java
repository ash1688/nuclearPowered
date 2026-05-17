package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.RegistryObject;

/**
 * Shared menu base for all single-block machines.
 *
 * Slot ordering convention:
 *  - Indexes 0..(inputCount-1)        : machine input slots
 *  - Indexes inputCount..(inputCount+outputCount-1) : machine output slots
 *  - Then 36 player inventory slots (3 rows of 9 + hotbar)
 *
 * Subclasses just pass slot coordinates and the appropriate MenuType + Block ref.
 */
public abstract class BaseMachineMenu extends AbstractContainerMenu {
    protected final AbstractMachineBlockEntity blockEntity;
    protected final ContainerLevelAccess access;
    protected final ContainerData data;
    protected final RegistryObject<Block> blockRef;
    protected final int totalMachineSlots;

    public BaseMachineMenu(MenuType<?> type, int id, Inventory playerInv,
                           AbstractMachineBlockEntity be, RegistryObject<Block> blockRef,
                           int[][] inputCoords, int[][] outputCoords) {
        super(type, id);
        this.blockEntity = be;
        this.access = ContainerLevelAccess.create(be.getLevel(), be.getBlockPos());
        this.blockRef = blockRef;
        this.totalMachineSlots = inputCoords.length + outputCoords.length;

        for (int i = 0; i < inputCoords.length; i++) {
            addSlot(new SlotItemHandler(be.getInventory(), i, inputCoords[i][0], inputCoords[i][1]));
        }
        for (int i = 0; i < outputCoords.length; i++) {
            final int slotIdx = inputCoords.length + i;
            addSlot(new SlotItemHandler(be.getInventory(), slotIdx, outputCoords[i][0], outputCoords[i][1]) {
                @Override public boolean mayPlace(ItemStack stack) { return false; }
            });
        }
        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }

        this.data = new ContainerData() {
            @Override public int get(int index) {
                return switch (index) {
                    case 0 -> blockEntity.getProgress();
                    case 1 -> blockEntity.getMaxProgress();
                    case 2 -> blockEntity.getEnergyStored();
                    case 3 -> blockEntity.getEnergyCapacity();
                    case 4 -> blockEntity.getInputFluidTank() != null ? blockEntity.getInputFluidTank().getFluidAmount() : 0;
                    case 5 -> blockEntity.getInputFluidTank() != null ? blockEntity.getInputFluidTank().getCapacity() : 0;
                    case 6 -> blockEntity.getOutputFluidTank() != null ? blockEntity.getOutputFluidTank().getFluidAmount() : 0;
                    case 7 -> blockEntity.getOutputFluidTank() != null ? blockEntity.getOutputFluidTank().getCapacity() : 0;
                    default -> 0;
                };
            }
            @Override public void set(int index, int value) {}
            @Override public int getCount() { return 8; }
        };
        addDataSlots(data);
    }

    public int progress() { return data.get(0); }
    public int maxProgress() { return data.get(1); }
    public int energy() { return data.get(2); }
    public int maxEnergy() { return data.get(3); }
    public int inputFluidAmount() { return data.get(4); }
    public int inputFluidCapacity() { return data.get(5); }
    public int outputFluidAmount() { return data.get(6); }
    public int outputFluidCapacity() { return data.get(7); }

    public AbstractMachineBlockEntity blockEntity() { return blockEntity; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();
        int inputCount = blockEntity.getInputSlotCount();

        if (index < totalMachineSlots) {
            if (!moveItemStackTo(original, totalMachineSlots, totalMachineSlots + 36, true)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(original, 0, inputCount, false)) return ItemStack.EMPTY;
        }
        if (original.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, blockRef.get());
    }

    /** Decode the block pos from network and resolve the BlockEntity on the client. */
    protected static AbstractMachineBlockEntity readBE(FriendlyByteBuf buf, Inventory playerInv) {
        BlockPos pos = buf.readBlockPos();
        var be = playerInv.player.level().getBlockEntity(pos);
        if (!(be instanceof AbstractMachineBlockEntity machine)) {
            throw new IllegalStateException("BlockEntity at " + pos + " is not a machine");
        }
        return machine;
    }

    /** Unused — kept to satisfy import. */
    @SuppressWarnings("unused")
    private static NPBlocks unusedRef() { return null; }
}
