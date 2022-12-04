package com.hrznstudio.titanium.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class NBTUtil {
    public static CompoundTag tagFromStack(ItemStack stack){
        CompoundTag tag = new CompoundTag();
        stack.save(tag);
        return tag;
    }
}
