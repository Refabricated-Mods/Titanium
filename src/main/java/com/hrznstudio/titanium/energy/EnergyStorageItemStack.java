/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

public class EnergyStorageItemStack implements EnergyStorage {
    private static final String ENERGY = "stored";
    private static final String MAX = "max";
    private static final String INPUT = "in";
    private static final String OUTPUT = "out";
    private final ItemStack stack;

    public EnergyStorageItemStack(ItemStack stack, int capacity, int in, int out) {
        this.stack = stack;
        boolean hasTags = stack.hasTag();
        if (!hasTags || !stack.getTag().contains("energy")) {
            if (!hasTags) {
                stack.setTag(new CompoundTag());
            }
            CompoundTag tag = stack.getTag();
            CompoundTag energyTag = new CompoundTag();
            energyTag.putInt(ENERGY, 0);
            energyTag.putInt(MAX, capacity);
            energyTag.putInt(INPUT, in);
            energyTag.putInt(OUTPUT, out);
            tag.put("energy", energyTag);
        } else {
            CompoundTag energyTag = getStackEnergyTag();
            energyTag.putInt(MAX, capacity);
            energyTag.putInt(INPUT, in);
            energyTag.putInt(OUTPUT, out);
        }
    }

    public void putInternal(int energy) {
        CompoundTag energyTag = getStackEnergyTag();
        energyTag.putInt(ENERGY, Math.min(energyTag.getInt(ENERGY) + energy, energyTag.getInt(MAX)));
    }

    private CompoundTag getStackEnergyTag() {
        return stack.getTagElement("energy");
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;
        int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(getMaxReceive(), maxReceive));

        if (!simulate) {
            if (energyReceived != 0) {
                getStackEnergyTag().putInt("energy", getEnergyStored() + energyReceived);
            }
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;
        int energyExtracted = Math.min(getEnergyStored(), Math.min(getMaxExtract(), maxExtract));

        if (!simulate) {
            if (stack != null && energyExtracted != 0) {
                getStackEnergyTag().putInt("energy", getEnergyStored() - energyExtracted);
            }
        }
        return energyExtracted;
    }

    public int getMaxExtract() {
        return getStackEnergyTag().getInt(OUTPUT);
    }

    public int getMaxReceive() {
        return getStackEnergyTag().getInt(INPUT);
    }

    @Override
    public int getEnergyStored() {
        return getStackEnergyTag().getInt(ENERGY);
    }

    @Override
    public int getMaxEnergyStored() {
        return getStackEnergyTag().getInt(MAX);
    }

    @Override
    public boolean canExtract() {
        return getMaxExtract() > 0;
    }

    @Override
    public boolean canReceive() {
        return getMaxReceive() > 0;
    }
}
