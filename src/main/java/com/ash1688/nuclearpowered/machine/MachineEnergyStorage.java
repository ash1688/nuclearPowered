package com.ash1688.nuclearpowered.machine;

import net.minecraftforge.energy.EnergyStorage;

import java.util.function.Consumer;

/**
 * EnergyStorage that pings a callback whenever its state changes so the BlockEntity
 * can setChanged() / sync. Used by all machines for their FE buffer.
 */
public final class MachineEnergyStorage extends EnergyStorage {
    private final Consumer<Integer> onChange;

    public MachineEnergyStorage(int capacity, int maxReceive, Consumer<Integer> onChange) {
        super(capacity, maxReceive, 0);
        this.onChange = onChange;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (received > 0 && !simulate) onChange.accept(this.energy);
        return received;
    }

    /** Internal: machine ticking consumes energy directly. */
    public int useEnergy(int amount) {
        int used = Math.min(this.energy, amount);
        this.energy -= used;
        if (used > 0) onChange.accept(this.energy);
        return used;
    }

    public void setEnergyDirectly(int value) {
        this.energy = Math.max(0, Math.min(capacity, value));
    }
}
