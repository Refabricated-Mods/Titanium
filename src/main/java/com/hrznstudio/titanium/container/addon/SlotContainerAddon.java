/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.container.addon;

import com.google.common.collect.Lists;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Function;

public class SlotContainerAddon implements IContainerAddon {
    private final ItemStackHandler itemHandler;
    private final int xPos;
    private final int yPos;
    private final Function<Integer, Pair<Integer, Integer>> positionFunction;

    public SlotContainerAddon(ItemStackHandler itemHandler, int xPos, int yPos, Function<Integer, Pair<Integer, Integer>> positionFunction) {
        this.itemHandler = itemHandler;
        this.xPos = xPos;
        this.yPos = yPos;
        this.positionFunction = positionFunction;
    }

    @Override
    public List<Slot> getSlots() {
        List<Slot> slots = Lists.newArrayList();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            Pair<Integer, Integer> offset = positionFunction.apply(i);
            slots.add(new UpdatableSlotItemHandler(itemHandler, i, xPos + offset.getLeft(), yPos + offset.getRight()));
        }
        return slots;
    }
}
