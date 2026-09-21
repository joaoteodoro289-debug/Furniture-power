package com.example.furniturepower.tile;

import com.example.furniturepower.config.PowerConfig;
import com.example.furniturepower.energy.EnergyStorage;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public final class GeneratorTile extends TileEntity implements ITickable {
    private final EnergyStorage energy = new EnergyStorage(PowerConfig.capacity, PowerConfig.generatorTransfer, PowerConfig.generatorTransfer);
    private int fuelTicks;

    public int addFuel(ItemStack stack) {
        if (!isFuel(stack)) return 0;
        int ticksPerItem = stack.getMetadata() == 1 ? 2400 : 3200;
        int accepted = Math.min(ticksPerItem, 72000 - fuelTicks);
        if (accepted <= 0) return 0;
        fuelTicks += accepted;
        stack.shrink(1);
        markDirty();
        return accepted;
    }

    public boolean isFueled() { return fuelTicks > 0; }
    public int getFuelTicks() { return fuelTicks; }
    public EnergyStorage getEnergyStorage() { return energy; }

    @Override public void update() {
        if (world == null || world.isRemote) return;
        if (fuelTicks > 0 && energy.getEnergyStored() < energy.getMaxEnergyStored()) {
            fuelTicks--;
            energy.receiveEnergy(PowerConfig.generatorOutputPerTick, false);
            markDirty();
        }
        if (world.getTotalWorldTime() % 10 == 0) world.markBlockRangeForRenderUpdate(pos, pos);
    }

    @Override public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Override public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) return CapabilityEnergy.ENERGY.cast(energy);
        return super.getCapability(capability, facing);
    }

    @Override public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energy.write(tag, "Energy");
        tag.setInteger("FuelTicks", fuelTicks);
        return tag;
    }

    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.read(tag, "Energy");
        fuelTicks = Math.max(0, tag.getInteger("FuelTicks"));
    }

    public static boolean isFuel(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() == Items.COAL && (stack.getMetadata() == 0 || stack.getMetadata() == 1);
    }
}
