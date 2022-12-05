package com.hrznstudio.titanium.item;

import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public interface IStackHolderItem {
    Supplier<ItemStack> getHolder();
}
