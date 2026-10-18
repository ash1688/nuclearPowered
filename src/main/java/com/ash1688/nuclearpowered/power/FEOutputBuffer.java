package com.ash1688.nuclearpowered.power;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.EnergyStorage;

/**
 * FE source buffer for generators (Steam Engine now; Thermocouple and Turbogenerator later).
 * External blocks may extract but never insert; the owner adds FE with {@link #generate} and
 * calls {@link #pushToNeighbours} once per tick.
 */
public final class FEOutputBuffer extends EnergyStorage {
    private boolean dirty = false;

    public FEOutputBuffer(int capacity, int maxExtract) {
        super(capacity, 0, maxExtract);
    }

    /** Adds generated FE, clamped to free space. Returns the amount stored. */
    public int generate(int amount) {
        int added = Math.max(0, Math.min(amount, capacity - energy));
        energy += added;
        if (added > 0) dirty = true;
        return added;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (extracted > 0 && !simulate) dirty = true;
        return extracted;
    }

    /** Offers up to {@code limit} FE in total this tick to every adjacent FE receiver. Returns FE pushed. */
    public int pushToNeighbours(Level level, BlockPos pos, int limit) {
        int budget = Math.min(limit, energy);
        int pushed = 0;
        for (Direction dir : Direction.values()) {
            if (budget - pushed <= 0) break;
            BlockEntity neighbour = level.getBlockEntity(pos.relative(dir));
            if (neighbour == null) continue;
            final int offer = budget - pushed;
            pushed += neighbour.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite())
                    .map(target -> target.canReceive() ? target.receiveEnergy(offer, false) : 0)
                    .orElse(0);
        }
        if (pushed > 0) {
            energy -= pushed;
            dirty = true;
        }
        return pushed;
    }

    public void setEnergyDirectly(int value) {
        this.energy = Math.max(0, Math.min(capacity, value));
    }

    public int freeSpace() { return capacity - energy; }

    /** Returns true if state changed since the last call, and clears the flag. */
    public boolean isDirtyAndClear() {
        boolean was = dirty;
        dirty = false;
        return was;
    }
}
