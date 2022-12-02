/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.util;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import io.github.fabricators_of_create.porting_lib.util.FluidUtil;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * This class exists because @{@link FluidUtil}'s tryEmptyContainer doesn't work properly
 */
public class TitaniumFluidUtil {

    @Nonnull
    public static FluidActionResult tryEmptyContainer(@Nonnull ItemStack container, IFluidHandler fluidDestination, int maxAmount, boolean doDrain) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
        return FluidUtil.getFluidHandler(containerCopy)
            .map(containerFluidHandler -> {
                FluidStack transfer = FluidUtil.tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, doDrain);
                if (transfer.isEmpty()) {
                    return FluidActionResult.FAILURE;
                }
                ItemStack resultContainer = containerFluidHandler.getContainer();
                return new FluidActionResult(resultContainer);
            })
            .orElse(FluidActionResult.FAILURE);
    }

}
