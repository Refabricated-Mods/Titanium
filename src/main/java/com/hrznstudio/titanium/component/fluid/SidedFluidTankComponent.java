/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.component.fluid;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IAsset;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.IScreenAddonProvider;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.client.screen.addon.FacingHandlerScreenAddon;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.component.sideness.SidedComponentManager;
import com.hrznstudio.titanium.util.FacingUtil;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class SidedFluidTankComponent<T extends IComponentHarness> extends FluidTankComponent<T> implements IFacingComponent, IScreenAddonProvider {

    private int color;
    private int facingHandlerX = 8;
    private int facingHandlerY = 84;
    private HashMap<FacingUtil.Sideness, FaceMode> facingModes;
    private int pos;
    private boolean hasFacingAddon;
    private FaceMode[] validFaceModes;

    public SidedFluidTankComponent(String name, int amount, int posX, int posY, int pos) {
        super(name, amount, posX, posY);
        this.color = DyeColor.WHITE.getFireworkColor();
        this.facingModes = new HashMap<>();
        this.pos = pos;
        for (FacingUtil.Sideness facing : FacingUtil.Sideness.values()) {
            this.facingModes.put(facing, FaceMode.ENABLED);
        }
        this.hasFacingAddon = true;
        this.validFaceModes = FaceMode.values();
    }

    public SidedFluidTankComponent<T> disableFacingAddon() {
        this.hasFacingAddon = false;
        return this;
    }

    @Override
    public HashMap<FacingUtil.Sideness, FaceMode> getFacingModes() {
        return facingModes;
    }

    @Override
    public int getColor() {
        return new Color(color).getRGB();
    }

    public SidedFluidTankComponent<T> setColor(int color) {
        this.color = color;
        return this;
    }

    public SidedFluidTankComponent<T> setColor(DyeColor color) {
        this.color = color.getFireworkColor();
        return this;
    }

    @Override
    public Rectangle getRectangle(IAsset asset) {
        return new Rectangle(this.getPosX() - 2, this.getPosY() - 2, (int) asset.getArea().getWidth() + 3, (int) asset.getArea().getHeight() + 3);
    }

    @Override
    public int getFacingHandlerX() {
        return this.facingHandlerX;
    }

    @Override
    public int getFacingHandlerY() {
        return this.facingHandlerY;
    }


    @Override
    public boolean work(Level world, BlockPos pos, Direction blockFacing, int workAmount) {
        for (FacingUtil.Sideness sideness : facingModes.keySet()) {
            if (facingModes.get(sideness).equals(FaceMode.PUSH)) {
                Direction real = FacingUtil.getFacingFromSide(blockFacing, sideness);
                Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, pos, real.getOpposite());
                boolean hasWorked = storage != null && transfer(this, storage, workAmount);
                if (hasWorked) {
                    return true;
                }
            }
        }
        for (FacingUtil.Sideness sideness : facingModes.keySet()) {
            if (facingModes.get(sideness).equals(FaceMode.PULL)) {
                Direction real = FacingUtil.getFacingFromSide(blockFacing, sideness);
                Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, pos, real.getOpposite());
                boolean hasWorked = storage != null && transfer(storage, this, workAmount);
                if (hasWorked) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public SidedFluidTankComponent<T> setFacingHandlerPos(int x, int y) {
        this.facingHandlerX = x;
        this.facingHandlerY = y;
        return this;
    }


    @Override
    public FaceMode[] getValidFacingModes() {
        return validFaceModes;
    }

    public SidedFluidTankComponent<T> setValidFaceModes(FaceMode... validFaceModes){
        this.validFaceModes = validFaceModes;
        for (FacingUtil.Sideness value : FacingUtil.Sideness.values()) {
            this.facingModes.put(value, validFaceModes[0]);
        }
        return this;
    }

    private boolean transfer(Storage<FluidVariant> from, Storage<FluidVariant> to, long workAmount) {
        FluidStack stack = TransferUtil.simulateExtractAnyFluid(from,workAmount * 100);
        if (!stack.isEmpty()) {
            Transaction transaction = Transaction.openOuter();
            long drain = from.extract(stack.getType(), to.insert(stack.getType(), stack.getAmount(), transaction), transaction);
            if (drain > 0) transaction.commit();
            else transaction.abort();
            return drain > 0;
        }
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        List<IFactory<? extends IScreenAddon>> addons = super.getScreenAddons();
        if (hasFacingAddon)
            addons.add(this::createScreen);
        return addons;
    }

    @Override
    public FluidTank readFromNBT(CompoundTag nbt) {
        if (nbt.contains("FacingModes")) {
            CompoundTag compound = nbt.getCompound("FacingModes");
            for (String face : compound.getAllKeys()) {
                facingModes.put(FacingUtil.Sideness.valueOf(face), FaceMode.valueOf(compound.getString(face)));
            }
        }
        return super.readFromNBT(nbt);
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag comp) {
        CompoundTag nbt = super.writeToNBT(comp);
        CompoundTag compound = new CompoundTag();
        for (FacingUtil.Sideness facing : facingModes.keySet()) {
            compound.putString(facing.name(), facingModes.get(facing).name());
        }
        nbt.put("FacingModes", compound);
        return nbt;
    }

    @Environment(EnvType.CLIENT)
    private IScreenAddon createScreen() {
        return new FacingHandlerScreenAddon(SidedComponentManager.ofRight(getFacingHandlerX(), getFacingHandlerY(), pos, AssetTypes.BUTTON_SIDENESS_MANAGER, 4), this, getTankType().getAssetType(), this.getComponentHarness() instanceof ActiveTile ? ((ActiveTile) this.getComponentHarness()).getFacingDirection() : Direction.NORTH);
    }
}
