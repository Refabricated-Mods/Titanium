/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;


public class ItemHandlerUtil {

    @Nonnull
    public static ItemStack getFirstItem(Storage<ItemVariant> handler) {
        for (StorageView<ItemVariant> view : handler.iterable(null)){
            if (!view.isResourceBlank()){
                return view.getResource().toStack();
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean isEmpty(Storage<ItemVariant> handler) {
        for (StorageView<ItemVariant> view : handler.iterable(null)){
            if (!view.isResourceBlank()){
                return false;
            }
        }
        return true;
    }

}
