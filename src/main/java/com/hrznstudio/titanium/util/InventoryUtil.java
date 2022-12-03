/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.util;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class InventoryUtil {

    public static List<ItemStack> getStacks(@Nullable Storage<ItemVariant> handler) {
        if (handler == null)
            return Collections.emptyList();
        ImmutableList.Builder<ItemStack> builder = new ImmutableList.Builder<>();
        for (StorageView<ItemVariant> view : handler.iterable(null)) {
            ItemStack subStack = view.getResource().toStack();
            if (!subStack.isEmpty())
                builder.add(subStack);
        }
        return builder.build();
    }
}
