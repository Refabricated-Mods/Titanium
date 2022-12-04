/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.component.bundle;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.tile.BasicTile;
import com.hrznstudio.titanium.client.screen.addon.AssetScreenAddon;
import com.hrznstudio.titanium.component.IComponentBundle;
import com.hrznstudio.titanium.component.IComponentHandler;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import com.hrznstudio.titanium.util.TitaniumFluidUtil;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import io.github.fabricators_of_create.porting_lib.util.FluidUtil;
import io.github.fabricators_of_create.porting_lib.util.INBTSerializable;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class TankInteractionBundle<T extends BasicTile & IComponentHarness> implements IComponentBundle, INBTSerializable<CompoundTag> {

    private final Supplier<Storage<FluidVariant>> fluidHandler;
    private int posX;
    private int posY;
    private InventoryComponent<T> input;
    private InventoryComponent<T> output;
    private ProgressBarComponent<T> bar;

    public TankInteractionBundle(Supplier<Storage<FluidVariant>> fluidHandler, int posX, int posY, T componentHarness, int maxProgress) {
        this.fluidHandler = fluidHandler;
        this.posX = posX;
        this.posY = posY;
        this.input = new InventoryComponent<T>("tank_input", posX + 5, posY + 7, 1)
            .setSlotToItemStackRender(0, new ItemStack(Items.BUCKET))
            .setOutputFilter((stack, integer) -> false)
            .setSlotToColorRender(0, DyeColor.BLUE)
            .setInputFilter((stack, integer) -> TransferUtil.getFluidContained(stack).isPresent())
            .setComponentHarness(componentHarness);
        this.output = new InventoryComponent<T>("tank_output", posX + 5, posY + 60, 1)
            .setSlotToItemStackRender(0, new ItemStack(Items.BUCKET))
            .setInputFilter((stack, integer) -> false)
            .setSlotToColorRender(0, DyeColor.ORANGE)
            .setComponentHarness(componentHarness);
        this.bar = new ProgressBarComponent<T>(posX + 5, posY + 30, maxProgress)
            .setBarDirection(ProgressBarComponent.BarDirection.ARROW_DOWN)
            .setCanReset(t -> true)
            .setCanIncrease(t -> !this.input.getStackInSlot(0).isEmpty() && TitaniumFluidUtil.getFluidItemStorage(this.input.getStackInSlot(0)) != null && !getOutputStack(false).isEmpty() && (this.output.getStackInSlot(0).isEmpty() || ItemHandlerHelper.canItemStacksStack(getOutputStack(false), this.output.getStackInSlot(0))))
            .setOnFinishWork(() -> {
                ItemStack result = getOutputStack(false);
                Transaction transaction = Transaction.openOuter();
                if (this.output.simulateInsert(ItemVariant.of(result), 1, null) > 0) {
                    result = getOutputStack(true);
                    this.output.insert(ItemVariant.of(result), 1, transaction);
                    transaction.commit();
                    this.input.getStackInSlot(0).shrink(1);
                    componentHarness.setChanged();
                }
            })
            .setComponentHarness(componentHarness);

    }

    @Override
    public void accept(IComponentHandler... handler) {
        for (IComponentHandler iComponentHandler : handler) {
            iComponentHandler.add(this.input, this.output, this.bar);
        }
    }

    public ItemStack getOutputStack(boolean execute) {
        Storage<FluidVariant> storage = fluidHandler.get();
        if (storage == null) return ItemStack.EMPTY;
        ItemStack stack = this.input.getStackInSlot(0).copy();
        stack.setCount(1);
        TitaniumFluidUtil.FluidActionResult result = TitaniumFluidUtil.tryFillContainer(stack, storage, Integer.MAX_VALUE, null, execute);
        if (result.isSuccess()) return result.getResult();
        result = TitaniumFluidUtil.tryEmptyContainer(stack, storage, Integer.MAX_VALUE, execute);
        if (result.isSuccess()) return result.getResult();
        return ItemStack.EMPTY;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        return Collections.singletonList(() -> new AssetScreenAddon(AssetTypes.AUGMENT_BACKGROUND, posX, posY, true));
    }

    @Override
    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
        return Collections.emptyList();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundNBT = new CompoundTag();
        compoundNBT.put("Input", this.input.serializeNBT());
        compoundNBT.put("Output", this.output.serializeNBT());
        compoundNBT.put("Bar", this.bar.serializeNBT());
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.input.deserializeNBT(nbt.getCompound("Input"));
        this.output.deserializeNBT(nbt.getCompound("Output"));
        this.bar.deserializeNBT(nbt.getCompound("Bar"));
    }
}
