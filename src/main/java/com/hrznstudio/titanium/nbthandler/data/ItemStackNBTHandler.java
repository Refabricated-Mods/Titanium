/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.nbthandler.data;

import com.hrznstudio.titanium.api.INBTHandler;
import com.hrznstudio.titanium.util.NBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemStackNBTHandler implements INBTHandler<ItemStack> {
    @Override
    public boolean isClassValid(Class<?> aClass) {
        return ItemStack.class.isAssignableFrom(aClass);
    }

    @Override
    public boolean storeToNBT(@Nonnull CompoundTag compound, @Nonnull String name, @Nonnull ItemStack object) {
        compound.put(name, NBTUtil.tagFromStack(object));
        return true;
    }

    @Override
    public ItemStack readFromNBT(@Nonnull CompoundTag compound, @Nonnull String name, @Nullable ItemStack currentValue) {
        return ItemStack.of(compound.getCompound(name));
    }
}
