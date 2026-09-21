package com.example.furniturepower.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;

public final class EnergyStorage implements IEnergyStorage {
    private int energy;
    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;

    public EnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this.capacity = Math.max(1, capacity);
        this.maxReceive = Math.max(1, maxReceive);
        this.maxExtract = Math.max(1, maxExtract);
    }

    public void setEnergy(int value) { energy = Math.max(0, Math.min(capacity, value)); }
    public int getCapacity() { return capacity; }
    public NBTTagCompound write(NBTTagCompound tag, String key) { tag.setInteger(key, energy); return tag; }
    public void read(NBTTagCompound tag, String key) { setEnergy(tag.getInteger(key)); }

    @Override public int receiveEnergy(int amount, boolean simulate) {
        if (amount <= 0) return 0;
        int received = Math.min(amount, Math.min(maxReceive, capacity - energy));
        if (!simulate) energy += received;
        return received;
    }

    @Override public int extractEnergy(int amount, boolean simulate) {
        if (amount <= 0) return 0;
        int extracted = Math.min(amount, Math.min(maxExtract, energy));
        if (!simulate) energy -= extracted;
        return extracted;
    }

    @Override public int getEnergyStored() { return energy; }
    @Override public int getMaxEnergyStored() { return capacity; }
    @Override public boolean canExtract() { return true; }
    @Override public boolean canReceive() { return true; }
}
