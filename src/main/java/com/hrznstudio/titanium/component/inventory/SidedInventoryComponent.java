/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.component.inventory;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IAsset;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.client.screen.addon.FacingHandlerScreenAddon;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.component.sideness.SidedComponentManager;
import com.hrznstudio.titanium.util.FacingUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;

public class SidedInventoryComponent<T extends IComponentHarness> extends InventoryComponent<T> implements IFacingComponent {

    private int color;
    private int facingHandlerX = 8;
    private int facingHandlerY = 84;
    private HashMap<FacingUtil.Sideness, FaceMode> facingModes;
    private int position;
    private boolean hasFacingAddon;
    private FaceMode[] validFaceModes;

    public SidedInventoryComponent(String name, int xPos, int yPos, int size, int position) {
        super(name, xPos, yPos, size);
        this.color = DyeColor.WHITE.getFireworkColor();
        this.facingModes = new HashMap<>();
        for (FacingUtil.Sideness value : FacingUtil.Sideness.values()) {
            this.facingModes.put(value, FaceMode.ENABLED);
        }
        this.position = position;
        this.setColorGuiEnabled(true);
        this.hasFacingAddon = true;
        this.validFaceModes = FaceMode.values();
    }

    public SidedInventoryComponent<T> disableFacingAddon() {
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

    public SidedInventoryComponent<T> setColor(int color) {
        this.color = color;
        return this;
    }

    public SidedInventoryComponent<T> setColor(DyeColor color) {
        this.color = color.getMaterialColor().col;
        return this;
    }

    @Override
    public Color getColorForSlotRendering(int slot) {
        return getSlotToColorRenderMap().getOrDefault(slot, new Color(color));
    }

    @Override
    public Rectangle getRectangle(IAsset asset) {
        int renderingOffset = 1;
        Rectangle rectangle = new Rectangle(getSlotPosition().apply(0).getLeft() - 1, getSlotPosition().apply(0).getRight() - 1, (int) asset.getArea().getWidth() + getSlotPosition().apply(0).getLeft(), (int) asset.getArea().getHeight() + getSlotPosition().apply(0).getRight());
        for (int i = 0; i < getSlots(); i++) {
            if (getSlotPosition().apply(i).getLeft() < rectangle.getX()) {
                rectangle.setLocation(getSlotPosition().apply(i).getLeft(), rectangle.y);
            }
            if (getSlotPosition().apply(i).getRight() < rectangle.getY()) {
                rectangle.setLocation(rectangle.x, getSlotPosition().apply(i).getRight());
            }
            if (getSlotPosition().apply(i).getLeft() + asset.getArea().getWidth() > rectangle.width) {
                rectangle.setSize(getSlotPosition().apply(i).getLeft() + asset.getArea().width, rectangle.height);
            }
            if (getSlotPosition().apply(i).getRight() + asset.getArea().getHeight() > rectangle.height) {
                rectangle.setSize(rectangle.width, getSlotPosition().apply(i).getRight() + asset.getArea().height);
            }
        }
        return new Rectangle(this.getXPos() - renderingOffset - 2 + rectangle.x, this.getYPos() - renderingOffset - 2 + rectangle.y, rectangle.width + renderingOffset * 2 + 3, rectangle.height + renderingOffset * 2 + 3);
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
                Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, pos.relative(real), real.getOpposite());
                if (storage != null){
                    boolean hasWorked = transfer(sideness, this, storage, workAmount);
                    if (hasWorked) return true;
                }
            }
        }
        for (FacingUtil.Sideness sideness : facingModes.keySet()) {
            if (facingModes.get(sideness).equals(FaceMode.PULL)) {
                Direction real = FacingUtil.getFacingFromSide(blockFacing, sideness);
                Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, pos.relative(real), real.getOpposite());
                if (storage != null){
                    boolean hasWorked = transfer(sideness, storage, this, workAmount);
                    if (hasWorked) return true;
                }
            }
        }
        return false;
    }

    @Override
    public SidedInventoryComponent<T> setFacingHandlerPos(int x, int y) {
        this.facingHandlerX = x;
        this.facingHandlerY = y;
        return this;
    }

    @Override
    public FaceMode[] getValidFacingModes() {
        return validFaceModes;
    }

    public SidedInventoryComponent<T> setValidFaceModes(FaceMode... validFaceModes){
        this.validFaceModes = validFaceModes;
        for (FacingUtil.Sideness value : FacingUtil.Sideness.values()) {
            this.facingModes.put(value, validFaceModes[0]);
        }
        return this;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        CompoundTag compound = new CompoundTag();
        for (FacingUtil.Sideness facing : facingModes.keySet()) {
            compound.putString(facing.name(), facingModes.get(facing).name());
        }
        nbt.put("FacingModes", compound);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("FacingModes")) {
            CompoundTag compound = nbt.getCompound("FacingModes");
            for (String face : compound.getAllKeys()) {
                facingModes.put(FacingUtil.Sideness.valueOf(face), FaceMode.valueOf(compound.getString(face)));
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        List<IFactory<? extends IScreenAddon>> addons = super.getScreenAddons();
        if (hasFacingAddon)
            addons.add(this::createScreen);
        return addons;
    }

    private boolean transfer(FacingUtil.Sideness sideness, Storage<ItemVariant> from, Storage<ItemVariant> to, int workAmount) {
        Transaction transaction = Transaction.openOuter();
        for (StorageView<ItemVariant> view : from.iterable(transaction)){
            if (view.isResourceBlank()) continue;
            long extract = from.simulateExtract(view.getResource(), workAmount, null);
            if (extract > 0){
                long insert = to.insert(view.getResource(), extract, transaction);
                if (insert > 0){
                    long realExtract = from.extract(view.getResource(), insert, transaction);
                    if (realExtract > 0){
                        transaction.commit();
                        return true;
                    }
                }
            }
        }
        transaction.abort();
        return false;
    }

    @Environment(EnvType.CLIENT)
    private IScreenAddon createScreen() {
        return new FacingHandlerScreenAddon(SidedComponentManager.ofRight(getFacingHandlerX(), getFacingHandlerY(), position, AssetTypes.BUTTON_SIDENESS_MANAGER, 4), this, AssetTypes.SLOT, this.getComponentHarness() instanceof ActiveTile ? ((ActiveTile) this.getComponentHarness()).getFacingDirection() : Direction.NORTH);
    }
}
