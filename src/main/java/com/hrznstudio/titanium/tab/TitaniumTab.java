/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.tab;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class TitaniumTab{
    ResourceLocation label;
    protected Supplier<ItemStack> stackSupplier;
    protected final CreativeModeTab tab;

    public TitaniumTab(ResourceLocation label, Supplier<ItemStack> stackSupplier) {
        this.label = label;
        this.stackSupplier = stackSupplier;
        tab = FabricItemGroupBuilder.create(label).icon(this::getCurrentIcon).build();
    }

    public ItemStack getCurrentIcon() {
        return stackSupplier.get();
    }

    public CreativeModeTab getTab() {
        return tab;
    }
}
