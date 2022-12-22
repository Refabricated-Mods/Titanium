/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.util;

import com.google.common.base.Preconditions;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * This class exists because I am unaware of such a method in fabric
 */
//TODO remove once felt api pr is merged
public class TitaniumFluidUtil {

    @Nonnull
    public static FluidActionResult tryEmptyContainer(@Nonnull ItemStack container, Storage<FluidVariant> fluidDestination, long maxAmount, @Nullable Player player, boolean doDrain) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
        Optional<FluidStack> containerFirstFluid = TransferUtil.getFluidContained(containerCopy);
        return containerFirstFluid.map(f -> {
            Transaction transaction = Transaction.openOuter();
            ContainerItemContext context = ContainerItemContext.withInitial(containerCopy);
            Storage<FluidVariant> fluidItem = getFluidItemStorage(context);
            long fill = fluidDestination.insert(f.getType(), Math.min(f.getAmount(), maxAmount), transaction);
            if (fill > 0){
                long drain = fluidItem.extract(f.getType(), fill, transaction);
                if (drain > 0){
                    transaction.commit();
                    if (player != null)
                    {
                        SoundEvent soundevent = f.getFluid().getAttributes().getFillSound(f);
                        player.level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                    return new FluidActionResult(context.getItemVariant().toStack());
                }
            }
            transaction.abort();
            return FluidActionResult.FAILURE;
        }).orElse(FluidActionResult.FAILURE);
    }

    @Nonnull
    public static FluidActionResult tryFillContainer(@Nonnull ItemStack container, Storage<FluidVariant> fluidSource, long maxAmount, @Nullable Player player, boolean doDrain) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
        Transaction transaction = Transaction.openOuter();
        FluidStack stack = TransferUtil.simulateExtractAnyFluid(fluidSource, maxAmount);
        if (!stack.isEmpty()){
            ContainerItemContext context = ContainerItemContext.withInitial(containerCopy);
            Storage<FluidVariant> fluidItem = getFluidItemStorage(context);
            long fill = fluidItem.simulateInsert(stack.getType(), maxAmount, null);
            if (fill > 0){
                long drain = fluidSource.extract(stack.getType(), fill, transaction);
                if (drain > 0){
                    fluidItem.insert(stack.getType(), drain, transaction);
                    transaction.commit();
                    if (player != null)
                    {
                        SoundEvent soundevent = stack.getFluid().getAttributes().getFillSound(stack);
                        player.level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                    return new FluidActionResult(context.getItemVariant().toStack());
                }
            }
        }
        transaction.abort();
        return FluidActionResult.FAILURE;
    }

    public static boolean interactWithFluidHandler(@Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull Storage<FluidVariant> handler)
    {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(hand);
        Preconditions.checkNotNull(handler);

        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty())
        {
            //FluidActionResult fluidActionResult = tryFillContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
            FluidActionResult fluidActionResult = tryFillContainer(heldItem, handler, Long.MAX_VALUE, player, true);
            if (!fluidActionResult.isSuccess())
            {
                //fluidActionResult = tryEmptyContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                fluidActionResult = tryEmptyContainer(heldItem, handler, Long.MAX_VALUE, player, true);
            }

            if (fluidActionResult.isSuccess())
            {
                player.setItemInHand(hand, fluidActionResult.getResult());
                return true;
            }
            return false;
        }
        return false;
    }

    public static Storage<FluidVariant> getFluidItemStorage(ContainerItemContext context){
        return context.find(FluidStorage.ITEM);
    }

    public static Storage<FluidVariant> getFluidItemStorage(ItemStack stack){
        return getFluidItemStorage(ContainerItemContext.withInitial(stack));
    }


    public static class FluidActionResult
    {
        public static final FluidActionResult FAILURE = new FluidActionResult(false, ItemStack.EMPTY);

        public final boolean success;
        @Nonnull
        public final ItemStack result;

        public FluidActionResult(@Nonnull ItemStack result)
        {
            this(true, result);
        }

        private FluidActionResult(boolean success, @Nonnull ItemStack result)
        {
            this.success = success;
            this.result = result;
        }

        public boolean isSuccess()
        {
            return success;
        }

        @Nonnull
        public ItemStack getResult()
        {
            return result;
        }
    }
}
