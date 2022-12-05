/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.capability;

import com.hrznstudio.titanium.api.capability.IStackHolder;
import com.hrznstudio.titanium.item.IStackHolderItem;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CapabilityItemStackHolder {
    static ItemApiLookup<IStackHolder, ContainerItemContext> ITEM =
        ItemApiLookup.get(new ResourceLocation("titanium:stack_holder"), IStackHolder.class, ContainerItemContext.class);

    public static void init(){
        ITEM.registerFallback((s, c) -> {
            if (s.getItem() instanceof IStackHolderItem stackHolderItem) return new ItemStackHolderCapability(stackHolderItem.getHolder());
            return null;
        });
    }

}
