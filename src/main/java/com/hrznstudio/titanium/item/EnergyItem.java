/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.item;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleBatteryItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EnergyItem extends BasicItem implements SimpleBatteryItem {
    private final long capacity;
    private final long input;
    private final long output;

    public EnergyItem(String name, long capacity, long input, long output, Properties properties) {
        super(name, properties.stacksTo(1));
        this.capacity = capacity;
        this.input = input;
        this.output = output;
    }

    public EnergyItem(String name, Properties properties, long capacity, long throughput) {
        this(name, capacity, throughput, throughput, properties);
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == Key.SHIFT || super.hasTooltipDetails(key);
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, @Nonnull ItemStack stack, @Nonnull List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        if (key == Key.SHIFT) {
            //getEnergyStorage(stack).ifPresent(storage -> tooltip.add(new TextComponentString(TextFormatting.YELLOW + "Energy: " + TextFormatting.RED + storage.getEnergyStored() + TextFormatting.YELLOW + "/" + TextFormatting.RED + storage.getMaxEnergyStored() + TextFormatting.RESET))); TODO
        }
    }


    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getEnergyStorage(stack) != null;
    }

    @Override
    public int getBarWidth(ItemStack stack) { //TODO ???
        EnergyStorage storage = getEnergyStorage(stack);
        return storage == null ? 0 : (int) Math.round(1 - (double) storage.getAmount() / (double) storage.getCapacity() * 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00E93232;
    }

    public EnergyStorage getEnergyStorage(ItemStack stack) {
        return ContainerItemContext.withInitial(stack).find(EnergyStorage.ITEM);
    }

    @Override
    public long getEnergyCapacity() {
        return capacity;
    }

    @Override
    public long getEnergyMaxInput() {
        return input;
    }

    @Override
    public long getEnergyMaxOutput() {
        return output;
    }
}
