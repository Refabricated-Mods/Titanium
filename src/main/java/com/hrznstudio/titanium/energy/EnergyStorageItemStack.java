/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
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
    public long insert(long maxAmount, TransactionContext transaction) {
        if (!supportsInsertion()) return 0;
        long energyReceived = Math.min(getCapacity() - getAmount(), Math.min(getMaxReceive(), maxAmount));
        if (energyReceived > 0) transaction.addCloseCallback((t, r) -> {
            if (r.wasCommitted()) getStackEnergyTag().putLong("energy", getAmount() + energyReceived);
        });
        return energyReceived;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        if (!supportsExtraction()) return 0;
        long energyExtracted = Math.min(getAmount(), Math.min(getCapacity(), maxAmount));
        if (energyExtracted > 0) transaction.addCloseCallback((t, r) -> {
            if (r.wasCommitted()) getStackEnergyTag().putLong("energy", getAmount() - energyExtracted);
        });
        return energyExtracted;
    }

    public int getMaxExtract() {
        return getStackEnergyTag().getInt(OUTPUT);
    }

    public int getMaxReceive() {
        return getStackEnergyTag().getInt(INPUT);
    }

    @Override
    public long getAmount() {
        return getStackEnergyTag().getInt(ENERGY);
    }

    @Override
    public long getCapacity() {
        return getStackEnergyTag().getInt(MAX);
    }

    @Override
    public boolean supportsExtraction() {
        return getMaxExtract() > 0;
    }

    @Override
    public boolean supportsInsertion() {
        return getMaxReceive() > 0;
    }
}
