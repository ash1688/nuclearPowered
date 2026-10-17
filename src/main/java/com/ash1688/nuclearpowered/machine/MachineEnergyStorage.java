package com.ash1688.nuclearpowered.machine;

import net.minecraftforge.energy.EnergyStorage;

import java.util.function.Consumer;

/**
 * EnergyStorage that pings a callback whenever its state changes so the BlockEntity
 * can setChanged() / sync. Used by all machines for their FE buffer.
 *
 * Rate-limiting: receiveEnergy() can be called every tick (e.g. by a cable inserting
 * 200 FE/tick), and previously fired setChanged() on every call → 20 disk-marks/sec
 * per running machine. We now just set a dirty flag here, and the BE's tick method
 * flushes it via isDirtyAndClear() at most once per tick.
 */
public final class MachineEnergyStorage extends EnergyStorage {
    private final Consumer<Integer> onChange;
    private boolean dirty = false;

    public MachineEnergyStorage(int capacity, int maxReceive, Consumer<Integer> onChange) {
        super(capacity, maxReceive, 0);
        this.onChange = onChange;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (received > 0 && !simulate) dirty = true;
        return received;
    }

    /** Internal: machine ticking consumes energy directly. */
    public int useEnergy(int amount) {
        int used = Math.min(this.energy, amount);
        this.energy -= used;
        if (used > 0) dirty = true;
        return used;
    }

    public void setEnergyDirectly(int value) {
        this.energy = Math.max(0, Math.min(capacity, value));
    }

    /** Returns true if state changed since the last call, and clears the flag. */
    public boolean isDirtyAndClear() {
        if (dirty) {
            dirty = false;
            return true;
        }
        return false;
    }

    /** Returns the change-callback registered at construction (kept for parity). */
    public Consumer<Integer> onChange() { return onChange; }
}
